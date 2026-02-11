package edu.kit.ifv.populationsynthesis

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import edu.kit.ifv.populationsynthesis.datasource.CensusDemographyRuleCollector
import edu.kit.ifv.populationsynthesis.datasource.CensusHousehold
import edu.kit.ifv.populationsynthesis.datasource.CensusPerson
import edu.kit.ifv.populationsynthesis.datasource.parseResource

/*This annotation causes the CSV parser to ignore fields that are found in the csv, but not the data class.
 * Useful when we don't care about some fields. (Size for example is entirely useless once we construct the household).
 * If we want to be extra sure we can removethis annotation Then the program collapses if it finds a field in the
 * csv that is not represented by our data class. Helpful to make sure that we don't forget something
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class PersonInfo(
    @JsonProperty("ID") // Some properties have names in the CSV that we want to rename. This annotation serves that purpose
    val householdID: Long,
    val year: Int,
    val areaType: Int,
    @JsonProperty("birthyear")
    val birthYear: Int,
    //... val sex: Sex, etc.

) {
    /**
     *  This method serves to convert the [PersonInfo] object to a [CensusPerson]. Because in this version
     *  we decided to keep the Person info types as raw as possible, without any coding encoding or similar stuff
     *  that would happen here if we decide to encode something.
     */
    fun toCensusPerson(): CensusPerson {
        return CensusPerson(
            age = year - birthYear,
        )
    }

    companion object {
        private const val DEFAULT_RESOURCE = "personen_gesamt_DE_V2.csv"

        fun fromResource(resourceName: String = DEFAULT_RESOURCE): List<PersonInfo> {
            return parseResource<PersonInfo>(CensusDemographyRuleCollector::class.java, resourceName)
        }

    }
}

class Population(val households: List<CensusHousehold>) {


    companion object {
        fun fromPersonInfo(infos: Collection<PersonInfo> = PersonInfo.fromResource()): Population {
            val groupedInfos = infos.groupBy { it.householdID }
            val households = groupedInfos.map { (householdID, personInfos) ->
                CensusHousehold(
                    personInfos.map { it.toCensusPerson() }
                )
            }
            return Population(households)
        }
    }
}