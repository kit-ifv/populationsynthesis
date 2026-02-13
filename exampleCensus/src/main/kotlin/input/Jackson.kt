package edu.kit.ifv.populationsynthesis.input

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.csv.CsvGenerator
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

/**
 * Jackson module that installs CSV-specific deserialization rules used by our census import.
 *
 * ## Why this exists
 * Census-style CSV files are often “messy” compared to machine-generated exports:
 *
 * - Missing values may be represented as empty strings (`""`) rather than omitted columns.
 * - Special placeholders may appear (e.g. `"NA"`, `"-"`, `"/"`), depending on the data source.
 *
 * Jackson's default integer parsing is intentionally strict. That is great for clean datasets,
 * but it can make importing real-world CSV data fragile.
 *
 * This module wires in [LenientCensusIntDeserializer] for both Kotlin/Java `Int` variants:
 *
 * - `Int::class.java` is the primitive `int` on the JVM (non-nullable `Int` in Kotlin).
 * - `Int::class.javaObjectType` is the boxed `java.lang.Integer` (nullable `Int?` in Kotlin).
 *
 * ## Notes on Kotlin annotation targets
 * When mapping CSV headers to Kotlin properties, Jackson CSV may bind by *field/property*
 * rather than strictly by constructor parameter. If you rename columns using `@JsonProperty`,
 * prefer `@field:JsonProperty("...")` (and optionally also `@param:JsonProperty("...")`)
 * to ensure the name is visible on the bean property Jackson is actually using.
 *
 */

val module = SimpleModule().apply {
    addDeserializer(Int::class.java, LenientCensusIntDeserializer())
    addDeserializer(Int::class.javaObjectType, LenientCensusIntDeserializer())  // for nullable Int?
}


/**
 * Default [ObjectMapper] used for reading census CSV files into Kotlin data classes.
 *
 * What it does
 * - Uses [CsvMapper] (Jackson's CSV-aware mapper).
 * - Registers the Jackson Kotlin module so constructor-based data classes work naturally
 *   (non-null vs null, default values, etc.).
 * - Registers our custom [module] to make int parsing resilient.
 *
 */

internal val standardMapper: ObjectMapper = CsvMapper().registerKotlinModule().registerModule(module)

/**
 * Standard schema used for reading our census CSV files.
 *
 * What it does
 * - [CsvSchema.emptySchema] indicates we do not pre-declare columns programmatically.
 * - [CsvSchema.withHeader] tells Jackson to treat the first row as a header row and use it
 *   for column-to-property name matching.
 * - [CsvSchema.withColumnSeparator] sets `';'` as separator.
 *
 */
internal val standardSchema = CsvSchema.emptySchema().withHeader().withColumnSeparator(';')
internal inline fun <reified T> standardParse(path: Path): List<T> =
    path.toFile().inputStream().use { standardParse<T>(it) }

/**
 * Parse CSV data from an [input] stream into a list of Kotlin objects of type [T].
 *
 * ## What it does
 * - Creates a Jackson reader for `T`.
 * - Applies [standardSchema] (header row + semicolon separator).
 * - Streams records from the CSV and materializes them as a `List<T>`.
 *
 * ## Expected model shape
 * This works best when `T` is a “flat” data class, i.e., columns map to scalar properties
 * like `String`, `Int`, `Long`, `Double`, enums, etc.
 *
 * CSV is a tabular format and does **not** naturally support nested objects. If you encounter
 * errors like:
 * `CSV generator does not support Object values for properties (nested Objects)`
 * you have a few options:
 *
 * - Export one row per nested item (normalize into a separate CSV).
 * - Serialize nested objects into a single column as JSON text.
 * - Flatten a nested object into multiple columns (e.g. `area_id`, `area_name`, ...).
 *
 */
internal inline fun <reified T> standardParse(input: InputStream): List<T> {
    return standardMapper
        .readerFor(T::class.java)
        .with(standardSchema).readValues<T>(input)
        .readAll()
}
/**
 * Write a list of Kotlin objects [rows] to a CSV file at [path] using Jackson CSV.
 *
 * ## What it does
 * - Builds a dedicated [CsvMapper] with the Kotlin module enabled.
 * - Generates a CSV schema from the runtime type [T] via [CsvMapper.schemaFor].
 * - Writes a header row (column names) via [CsvSchema.withHeader].
 * - Emits the CSV to [path].
 *
 * ## Why this exists
 * Writing CSV “by hand” often becomes surprisingly tricky:
 * quoting rules, commas/newlines inside values, and consistent column naming/ordering are easy
 * to get wrong. Jackson handles these details and can write directly from your data classes.
 *
 * ## Column ordering
 * CSV consumers (Excel, pandas, etc.) typically care about predictable column order.
 * Jackson may reorder columns depending on configuration and introspection rules.
 * We enable [CsvSchema.withColumnReordering] to keep ordering stable *when possible*.
 *
 * If you need strict ordering, the most reliable approaches are:
 * - Add `@JsonPropertyOrder(...)` to your DTO used for export.
 * - Or build [CsvSchema] manually with columns in the exact order you want.
 *
 * ## Nested data warning
 * CSV is flat. If [T] contains nested objects or lists, Jackson CSV cannot write them as-is.
 * Prefer one of these strategies:
 * - Create a dedicated “flat” export DTO for CSV.
 * - Serialize nested objects as JSON strings in one column.
 * - Normalize into multiple CSVs (one-to-many relationship).
 *
 * ## Quoting behavior
 * We enable [CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS]. This makes exports robust when string
 * values may contain separators, newlines, or leading zeros.
 *
 * If you prefer smaller files, you can disable it and only quote when necessary.
 *
 * ## Usage
 * ```
 * writeCsv(Path.of("people.csv"), people)
 * ```
 */
inline fun <reified T : Any> writeCsv(path: Path, rows: List<T>) {
    val mapper = CsvMapper.builder()
        .addModule(KotlinModule.Builder().build())
        // optional: always quote strings, useful if you have commas/newlines
        .enable(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS)
        .build()

    val schema: CsvSchema = mapper
        .schemaFor(T::class.java)
        .withHeader()          // first row = column names
        .withColumnReordering(true) // keeps stable order (best effort)

    Files.newBufferedWriter(path).use { writer ->
        mapper.writer(schema).writeValue(writer, rows)
    }
}