package edu.kit.ifv.populationsynthesis.input.rules

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import edu.kit.ifv.populationsynthesis.domain.population.AgeGroupCode
import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.domain.population.Sex
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup

/**
 * This is a representation of our raw data input that we get from the census demography file.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class CensusDemographyData(
    @JsonProperty("0_Insgesamt_") val Insgesamt_: Int?, //Bevölkerung insgesamt (Anzahl)
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
) {
    val arsKey by lazy { ARSKey(_RS, Name) }

    fun getAgeTarget(intervalCode: AgeGroupCode): Int? = when (intervalCode.rawCode) {
        1 -> Alter_infr__01
        2 -> Alter_infr__02
        3 -> Alter_infr__03
        4 -> Alter_infr__04
        5 -> Alter_infr__05
        6 -> Alter_infr__06
        7 -> Alter_infr__07
        8 -> Alter_infr__08
        9 -> Alter_infr__09
        10 -> Alter_infr__10
        11 -> Alter_infr__11
        else -> throw IllegalArgumentException("Invalid intervalCode $intervalCode")
    }

    fun getSexTarget(sex: Sex): Int? = when(sex) {
        Sex.Companion.MALE -> GESCHLECHT__1
        Sex.Companion.FEMALE -> GESCHLECHT__2
        else -> throw IllegalArgumentException("Cannot construct a target from sex $sex")
    }

}