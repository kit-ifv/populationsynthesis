package edu.kit.ifv.populationsynthesis.datasource

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import edu.kit.ifv.populationsynthesis.datasource.input.ARSKey
import edu.kit.ifv.populationsynthesis.datasource.input.standardParse
import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition
import edu.kit.ifv.populationsynthesis.rules.measurement.MeasurementDefinition
import edu.kit.ifv.populationsynthesis.rules.measurement.NamedMeasurement
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
    val Insgesamt_val: String?, // Haushalte insgesamt (Anzahl)
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
    )

    private val definitions = (0..4).map {
        HouseholdSizeDefinition(it, HouseholdSizeDefinition.EqualityOp.EQUALS)
    }

    fun sizeRules(): CoverageGroup<CensusHousehold> {
        val equalRules = targetValues.zip(definitions).filter { it.first != null }.map { (target, definition) ->
            definition.makeRule(target!!.toDouble())
        }

        val greaterTarget = HH_SIZE_NAT__5 + HH_SIZE_NAT__6

        val geRule = greaterTarget?.let {
            HouseholdSizeDefinition(5, HouseholdSizeDefinition.EqualityOp.GREATER_OR_EQUAL).makeRule(it.toDouble())
        }
        val potentialRules = (equalRules + geRule).filterNotNull()
        return ExplicitTargetCoverageGroup(rules = potentialRules.toRuleSet(), totalTarget = Insgesamt_val!!.toDouble())
    }

    fun expandedSizeRules(): CoverageGroup<CensusHousehold> {
        return ExplicitTargetCoverageGroup()
    }
    operator fun Int?.plus(other: Int?): Int? {
        return this?.plus(other?:0) ?: other
    }
    fun familienTypRules(): CoverageGroup<CensusHousehold> = TODO()
    fun lebenstypRules(): CoverageGroup<CensusHousehold> = TODO()
    fun seniorenTypRules(): CoverageGroup<CensusHousehold> = TODO()
}


object HouseholdRuleFactory : MeasurementDefinition<CensusHousehold> {

    enum class EqualityOp(val symbol: String) {
        EQUALS("=="),
        NOT_EQUALS("!="),
        LESS_THAN("<"),
        LESS_OR_EQUAL("<="),
        GREATER_THAN(">"),
        GREATER_OR_EQUAL(">=")
    }

    fun createLogic(size: Int, operator: EqualityOp) {

    }

    override fun createNamedMeasurement(): NamedMeasurement<CensusHousehold> {
        TODO("Not yet implemented")
    }
}

class HouseholdSizeDefinition(val targetSize: Int, val equalityOp: EqualityOp) :
    BooleanMeasurementDefinition<CensusHousehold>() {
    override fun generateDescription(): String {
        return "Household $targetSize $equalityOp"
    }

    override fun evaluation(element: CensusHousehold): Boolean {
        return equalityOp.test(element.members.size, targetSize)

    }

    enum class EqualityOp(val symbol: String) {
        EQUALS("==") {
            override fun <T : Comparable<T>> test(a: T, b: T): Boolean {
                return a == b
            }

        },
        NOT_EQUALS("!=") {
            override fun <T : Comparable<T>> test(a: T, b: T): Boolean {
                return a != b
            }
        },
        LESS_THAN("<") {
            override fun <T : Comparable<T>> test(a: T, b: T): Boolean {
                return a < b
            }
        },
        LESS_OR_EQUAL("<=") {
            override fun <T : Comparable<T>> test(a: T, b: T): Boolean {
                return a <= b
            }
        },
        GREATER_THAN(">") {
            override fun <T : Comparable<T>> test(a: T, b: T): Boolean {
                return a > b
            }
        },
        GREATER_OR_EQUAL(">=") {
            override fun <T : Comparable<T>> test(a: T, b: T): Boolean {
                return a >= b
            }
        };

        abstract fun <T : Comparable<T>> test(a: T, b: T): Boolean
    }
}

class CensusHouseholdRuleCollector(
    map: Map<ARSKey, CensusHouseholdData>
) : CensusDataset<CensusHouseholdData, CensusHousehold>(map) {
    val sizeProvider by lazy {
        buildExhaustiveProvider(CensusHouseholdData::sizeRules)
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
}

internal fun resourceStream(owner: Class<*>, name: String): InputStream =
    requireNotNull(owner.classLoader.getResourceAsStream(name)) {
        "Resource not found on classpath: $name"
    }

internal inline fun <reified T> parseResource(owner: Class<*>, resourceName: String): List<T> =
    resourceStream(owner, resourceName).use { standardParse<T>(it) }

internal val CensusRootPath = Path("exampleCensus/src/main/resources/census")