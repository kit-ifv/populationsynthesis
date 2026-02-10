package edu.kit.ifv.populationsynthesis.datasource

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import edu.kit.ifv.populationsynthesis.datasource.input.ARSKey
import edu.kit.ifv.populationsynthesis.datasource.input.standardParse
import edu.kit.ifv.populationsynthesis.rules.contribution.BooleanContributionDefinition
import edu.kit.ifv.populationsynthesis.rules.contribution.NamedContribution
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.covered.ExplicitTargetCoverageGroup
import edu.kit.ifv.populationsynthesis.rules.toRuleSet
import java.io.InputStream

/**
 * This is
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class CensusDemography(
    @JsonProperty("0_Insgesamt_")
    val Insgesamt_: Int?, //Bevölkerung insgesamt (Anzahl)
    val GESCHLECHT__1: Int?, //Bevölkerung männlich (Anzahl)
    val GESCHLECHT__2: Int?, //Bevölkerung weiblich (Anzahl)
    val Staatsange_kurz__1: Int?, //Personen mit deutscher Staatsangehörigkeit (Anzahl)
    val Staatsange_kurz__2: Int?, //Personen mit ausländischer Staatsangehörigkeit (einschl. Personen mit einer Staatsangehörigkeit „Staatenlos“, „Ungeklärt“ und „Ohne Angabe“) (Anzahl)
    val Alter_infr__01: Int?, //Personen in der Altersklasse "Unter 3 Jahren" (Anzahl)
    val Alter_infr__02: Int?, //Personen in der Altersklasse "3 - 5 Jahre" (Anzahl)
    val Alter_infr__03: Int?, //Personen in der Altersklasse "6 - 9 Jahre" (Anzahl)
    val Alter_infr__04: Int?, //Personen in der Altersklasse "10 - 15 Jahre" (Anzahl)
    val Alter_infr__05: Int?, //Personen in der Altersklasse "16 - 18 Jahre" (Anzahl)
    val Alter_infr__06: Int?, //Personen in der Altersklasse "19 - 24 Jahre" (Anzahl)
    val Alter_infr__07: Int?, //Personen in der Altersklasse "25 - 39 Jahre" (Anzahl)
    val Alter_infr__08: Int?, //Personen in der Altersklasse "40 - 59 Jahre" (Anzahl)
    val Alter_infr__09: Int?, //Personen in der Altersklasse "60 - 66 Jahre" (Anzahl)
    val Alter_infr__10: Int?, //Personen in der Altersklasse "67 - 74 Jahre" (Anzahl)
    val Alter_infr__11: Int?, //Personen in der Altersklasse "75 Jahre und älter" (Anzahl)
    val FAMSTND_KURZ__1: Int?, //Personen mit dem Familienstand "Ledig" (Anzahl)
    val FAMSTND_KURZ__2: Int?, //Personen mit dem Familienstand "Verheiratet / eingetragene Lebenspartnerschaft" (Anzahl)
    val FAMSTND_KURZ__3: Int?, //Personen mit dem Familienstand "Verwitwet / eingetragene(r) Lebenspartner(in) verstorben" (Anzahl)
    val FAMSTND_KURZ__4: Int?, //Personen mit dem Familienstand "Geschieden / eingetragene Lebenspartnerschaft aufgehoben" (Anzahl)
    val FAMSTND_KURZ__5: Int?, //Personen mit dem Familienstand "Ohne Angabe" (Anzahl)
    val Einwanderungserf_ausf__2: Int?, //Personen mit Einwanderungsgeschichte (Einwanderungserf_ausf__21 und Einwanderungserf_ausf__22 zusammen) (Anzahl)
    val Einwanderungserf_ausf__21: Int?, //Eingewanderte (Anzahl)
    val Einwanderungserf_ausf__22: Int?, //Nachkommen von Eingewanderten (Anzahl)
    val Einwanderungserf_ausf__3: Int?, //Personen mit einseitiger Einwanderungsgeschichte (Anzahl)
    val Einwanderungserf_ausf__1: Int?, //Personen ohne Einwanderungsgeschichte (Anzahl)
    val _RS: String,
    val Name: String,
)  {
    val arsKey by lazy { ARSKey(_RS, Name) }
    private val intervals = listOf(
        0..2,
        3..5,
        6..9,
        10..15,
        16..18,
        19..24,
        25..39,
        40..59,
        60..66,
        67..74,
    )

    private val targetValues = listOf(
        Alter_infr__01,
        Alter_infr__02,
        Alter_infr__03,
        Alter_infr__04,
        Alter_infr__05,
        Alter_infr__06,
        Alter_infr__07,
        Alter_infr__08,
        Alter_infr__09,
        Alter_infr__10,
        Alter_infr__11,
    )

    fun ageRules(): CoverageGroup<CensusHousehold> {

        val rules = targetValues.zip(intervals).filter { it.first != null }.map { (target, interval) ->
            AgeRuleFactory.createLogic(interval).withTarget(target!!.toDouble())
        }
        return ExplicitTargetCoverageGroup(rules.toRuleSet(), Insgesamt_!!.toDouble())
    }


    fun familientandRules(): CoverageGroup<CensusHousehold> {
        return TODO()
    }
    fun sexRules(): CoverageGroup<CensusHousehold> {
        return TODO()
    }

    fun staatsAngehoerigkeitRules(): CoverageGroup<CensusHousehold> {

        return TODO()
    }

    fun einwanderungserf_ausf(): CoverageGroup<CensusHousehold> {
        return TODO()
    }


}
object AgeRuleFactory {
    fun createLogic(acceptedInterval: IntRange): NamedContribution<CensusHousehold> {
        return NamedContribution.numeric("Age Rule [${acceptedInterval.first}..${acceptedInterval.last}]") { household ->
            household.members.count { it.age in acceptedInterval }
        }
    }
}

class AgeDefinition(val range: IntRange): BooleanContributionDefinition<CensusPerson>() {
    override fun generateDescription(): String {
        return "Age in [${range.first}..${range.last}]"
    }

    override fun evaluation(element: CensusPerson): Boolean {
        return element.age in range
    }
}


/**
 * Has 5 different rule Providers that can be built from census data set found at [TODO add file]
 */
class CensusDemographyRuleCollector(
     map: Map<ARSKey, CensusDemography>
) : CensusDataset<CensusDemography, CensusHousehold>(map) {

    val ageProvider by lazy {
        buildExhaustiveProvider(CensusDemography::ageRules)
    }



    val familienstandProvider by lazy {
        buildExhaustiveProvider(CensusDemography::familientandRules)
    }
    val staatsangehoerigkeitProvider by lazy {
        buildExhaustiveProvider(CensusDemography::staatsAngehoerigkeitRules)
    }
    val einwanderungserfProvider by lazy {
        buildExhaustiveProvider(CensusDemography::einwanderungserf_ausf)
    }
    val sexProvider by lazy {
        buildExhaustiveProvider(CensusDemography::sexRules)
    }
    operator fun get(arsKey: ARSKey): CensusDemography? {
        return map[arsKey]
    }

    companion object {
        private const val DEFAULT_RESOURCE = "census/Regionaltabelle_Demografie.csv"



        fun fromResource(resourceName: String = DEFAULT_RESOURCE): CensusDemographyRuleCollector {
            val output = parseResource<CensusDemography>(CensusDemographyRuleCollector::class.java, resourceName)
            return CensusDemographyRuleCollector(output.associateBy { it.arsKey })
        }
    }
}

