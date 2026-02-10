package edu.kit.ifv.populationsynthesis.datasource.input

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.InputStream
import java.nio.file.Path

val module = SimpleModule().apply {
    addDeserializer(Int::class.java, LenientCensusIntDeserializer())
    addDeserializer(Int::class.javaObjectType, LenientCensusIntDeserializer())  // for nullable Int?
}
internal val standardMapper: ObjectMapper = CsvMapper().registerKotlinModule().registerModule(module)

internal val standardSchema = CsvSchema.emptySchema().withHeader().withColumnSeparator(';')
internal inline fun <reified T> standardParse(path: Path): List<T> =
    path.toFile().inputStream().use { standardParse<T>(it) }
internal inline fun <reified T> standardParse(input: InputStream): List<T> {
    return standardMapper
        .readerFor(T::class.java)
        .with(standardSchema).readValues<T>(input)
        .readAll()
}