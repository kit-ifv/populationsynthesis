package edu.kit.ifv.populationsynthesis.datasource

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import edu.kit.ifv.populationsynthesis.datasource.input.ARSKey
import edu.kit.ifv.populationsynthesis.datasource.input.standardParse
import edu.kit.ifv.populationsynthesis.measurements.HouseholdSizeDefinition
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.covered.ExplicitTargetCoverageGroup
import edu.kit.ifv.populationsynthesis.rules.provider.MutableExhaustiveRuleProvider
import edu.kit.ifv.populationsynthesis.rules.toRuleSet
import java.io.InputStream
import kotlin.io.path.Path


@JsonIgnoreProperties(ignoreUnknown = true)
data class CensusHouseholdData(

    val _RS: String,
    val Name: String,
    val Reg_Ebene: String,
    @JsonProperty("0_Insgesamt_")
    val Insgesamt_val: Int?, // Haushalte insgesamt (Anzahl)
    val HH_SIZE_NAT__1: Int?, // Haushalte mit einer Person (Anzahl)
    val HH_SIZE_NAT__2: Int?, // Haushalte mit zwei Personen (Anzahl)
    val HH_SIZE_NAT__3: Int?, // Haushalte mit drei Personen (Anzahl)
    val HH_SIZE_NAT__4: Int?, // Haushalte mit vier Personen (Anzahl)
    val HH_SIZE_NAT__5: Int?, // Haushalte mit fünf Personen (Anzahl)
    val HH_SIZE_NAT__6: Int?, // Haushalte mit sechs Personen (Anzahl)
    val HHTYP_FAM__1: Int?, // Einpersonenhaushalt (Singlehaushalt) (Anzahl)
    val HHTYP_FAM__2: Int?, // Paare ohne Kind (Anzahl Haushalte)
    val HHTYP_FAM__3: Int?, // Paare mit Kind(ern) (Anzahl Haushalte)
    val HHTYP_FAM__4: Int?, // Alleinerziehende Elternteile (Anzahl Haushalte)
    val HHTYP_FAM__5: Int?, // Mehrpersonenhaushalte ohne Kernfamilie (Anzahl)
    val HHTYP_LEB__1: Int?, // Einpersonenhaushalte (Singlehaushalte) (Anzahl)
    val HHTYP_LEB__2: Int?, // Ehepaare (Anzahl Haushalte)
    val HHTYP_LEB__3: Int?, // Eingetragene Lebenspartnerschaften (Anzahl Haushalte)
    val HHTYP_LEB__4: Int?, // Nichteheliche Lebensgemeinschaften (Anzahl Haushalte)
    val HHTYP_LEB__5: Int?, // Alleinerziehende Mütter (Anzahl Haushalte)
    val HHTYP_LEB__6: Int?, // Alleinerziehende Väter (Anzahl Haushalte)
    val HHTYP_LEB__7: Int?, // Mehrpersonenhaushalte ohne Kernfamilie (Anzahl)
    val HHTYP_SENIOR_HH__1: Int?, // Haushalte mit ausschließlich Seniorinnen/Senioren (Anzahl)
    val HHTYP_SENIOR_HH__2: Int?, // Haushalte mit Seniorinnen/Senioren und Jüngeren (Anzahl)
    val HHTYP_SENIOR_HH__3: Int?, // Haushalte ohne Seniorinnen/Senioren (Anzahl)

) {
    val arsKey by lazy { ARSKey(_RS, Name) }

    private val targetValues = listOf(
        HH_SIZE_NAT__1,
        HH_SIZE_NAT__2,
        HH_SIZE_NAT__3,
        HH_SIZE_NAT__4,
        HH_SIZE_NAT__5,
        HH_SIZE_NAT__6,
    )

    private val definitions = (1..5).map {
        HouseholdSizeDefinition(it, HouseholdSizeDefinition.EqualityOp.EQUALS)
    }

    /**
     * All HH sizes to 4 with equals, the rest falls into a larger category.
     */
    fun sizeRules(): CoverageGroup<CensusHousehold> {
        return buildSizeCoverageGroup(4)
    }
    /**
     * All HH sizes to 5 with equals, the rest falls into a larger category.
     */
    fun expandedSizeRules(): CoverageGroup<CensusHousehold> {
        return buildSizeCoverageGroup(5)

    }

    private fun buildSizeCoverageGroup(lastExplicitSize: Int): CoverageGroup<CensusHousehold> {
        val defs = definitions.take(lastExplicitSize) + HouseholdSizeDefinition(
            lastExplicitSize + 1,
            HouseholdSizeDefinition.EqualityOp.GREATER_OR_EQUAL
        )
        val targets = targetValues.take(lastExplicitSize) + targetValues.drop(lastExplicitSize).fold(null) { a, b ->
            a + b

        }
        val rules = defs.zip(targets).mapNotNull { (definition, target) ->

            target?.let {
                definition.makeRule(it)
            }

        }.toRuleSet()
        return ExplicitTargetCoverageGroup(rules = rules, target = requireNotNull(Insgesamt_val))
    }
    operator fun Int?.plus(other: Int?): Int? {
        return this?.plus(other?:0) ?: other
    }
    fun familienTypRules(): CoverageGroup<CensusHousehold> = TODO()
    fun lebenstypRules(): CoverageGroup<CensusHousehold> = TODO()
    fun seniorenTypRules(): CoverageGroup<CensusHousehold> = TODO()
}

class CensusHouseholdRuleCollector(
    map: Map<ARSKey, CensusHouseholdData>
) : CensusDataset<CensusHouseholdData, CensusHousehold>(map) {
    val sizeProvider by lazy {
        buildExhaustiveProvider(CensusHouseholdData::sizeRules)
    }

    val size6plusProvider by lazy {
        buildExhaustiveProvider(CensusHouseholdData::expandedSizeRules)
    }


    val familienTypProvider by lazy {
        buildExhaustiveProvider(CensusHouseholdData::familienTypRules)
    }

    val lebenstypProvider by lazy {
        buildExhaustiveProvider(CensusHouseholdData::lebenstypRules)
    }

    val seniorenTypProvider by lazy {
        buildExhaustiveProvider(CensusHouseholdData::seniorenTypRules)
    }

    companion object {

        private const val DEFAULT_RESOURCE = "census/Regionaltabelle_Haushalte.csv"

        fun fromResource(resourceName: String = DEFAULT_RESOURCE): CensusHouseholdRuleCollector {
            val output = parseResource<CensusHouseholdData>(CensusHouseholdRuleCollector::class.java, resourceName)
            return CensusHouseholdRuleCollector(output.associateBy { it.arsKey })
        }
    }
}

abstract class CensusDataset<I, O>(protected val map: Map<ARSKey, I>) {
    protected fun buildExhaustiveProvider(extractor: I.() -> CoverageGroup<O>) =
        MutableExhaustiveRuleProvider<ARSKey, O>().apply {
            map.forEach { (key, value) ->
                add(key, value.extractor())
            }
        }

    operator fun get(arsKey: ARSKey): I? {
        return map[arsKey]
    }
}

internal fun resourceStream(owner: Class<*>, name: String): InputStream =
    requireNotNull(owner.classLoader.getResourceAsStream(name)) {
        "Resource not found on classpath: $name"
    }

internal inline fun <reified T> parseResource(owner: Class<*>, resourceName: String): List<T> =
    resourceStream(owner, resourceName).use { standardParse<T>(it) }

internal val CensusRootPath = Path("exampleCensus/src/main/resources/census")