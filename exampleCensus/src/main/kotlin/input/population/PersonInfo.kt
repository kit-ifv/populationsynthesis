package edu.kit.ifv.populationsynthesis.input.population

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import edu.kit.ifv.populationsynthesis.domain.rules.CensusDemographyRules
import edu.kit.ifv.populationsynthesis.input.parseResource
import edu.kit.ifv.populationsynthesis.domain.population.CensusPerson
import edu.kit.ifv.populationsynthesis.domain.population.HouseholdID
import edu.kit.ifv.populationsynthesis.domain.population.Sex

/*This annotation causes the CSV parser to ignore fields that are found in the csv, but not the data class.
 * Useful when we don't care about some fields. (Size for example is entirely useless once we construct the household).
 * If we want to be extra sure we can removethis annotation Then the program collapses if it finds a field in the
 * csv that is not represented by our data class. Helpful to make sure that we don't forget something
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class PersonInfo(
    @field:JsonProperty("ID") // Some properties have names in the CSV that we want to rename. This annotation serves that purpose
    val householdID: HouseholdID,
    val year: Int,
    val areaType: Int,
    @field:JsonProperty("birthyear")
    val birthYear: Int,
    val sex: Sex,


    ) {
    /**
     *  This method serves to convert the [PersonInfo] object to a [edu.kit.ifv.populationsynthesis.domain.population.CensusPerson]. Because in this version
     *  we decided to keep the Person info types as raw as possible, without any coding encoding or similar stuff
     *  that would happen here if we decide to encode something.
     */
    fun toCensusPerson(): CensusPerson {
        return CensusPerson(
            age = year - birthYear,
            sex = sex
        )
    }

    companion object {
        private const val DEFAULT_RESOURCE = "personen_gesamt_DE_V2.csv"

        fun fromResource(resourceName: String = DEFAULT_RESOURCE): List<PersonInfo> {
            return parseResource<PersonInfo>(CensusDemographyRules::class.java, resourceName)
        }

    }
}