package edu.kit.ifv.populationsynthesis.input.rules

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import edu.kit.ifv.populationsynthesis.domain.population.Sex
import edu.kit.ifv.populationsynthesis.domain.population.Sex.Companion.FEMALE
import edu.kit.ifv.populationsynthesis.domain.population.Sex.Companion.MALE
@JsonIgnoreProperties(ignoreUnknown = true)
data class CensusSchoolLevelData(
    val Berichtszeitpunkt: String, // Zensusergebnis zum Stichtag 15.05.2022 (Format JJJJMMTT)
    val _RS:String, // Amtlicher Regionalschlüssel (ARS)
    val Name: String, // Name der regionalen Einheit
    val Regionalebene:String, // Kategorie der regionalen Einheit
    val SCHUELER_KLSS_STP: Int?, // Personen in schulischer Ausbildung nach Klassenstufe "Insgesamt" insgesamt (Anzahl)
    val SCHUELER_KLSS_STP__M: Int?, // Personen in schulischer Ausbildung nach Klassenstufe "Insgesamt" männlich (Anzahl)
    val SCHUELER_KLSS_STP__W: Int?, // Personen in schulischer Ausbildung nach Klassenstufe "Insgesamt" weiblich (Anzahl)
    val SCHUELER_KLSS_STP__1: Int?, // Personen in schulischer Ausbildung nach Klassenstufe "Klasse 1 bis 4" insgesamt (Anzahl)
    val SCHUELER_KLSS_STP__1_M: Int?, // Personen in schulischer Ausbildung nach Klassenstufe "Klasse 1 bis 4" männlich (Anzahl)
    val SCHUELER_KLSS_STP__1_W: Int?, // Personen in schulischer Ausbildung nach Klassenstufe "Klasse 1 bis 4" weiblich (Anzahl)
    val SCHUELER_KLSS_STP__2: Int?, // Personen in schulischer Ausbildung nach Klassenstufe "Klasse 5 bis 9 bzw. 10" insgesamt (Anzahl)
    val SCHUELER_KLSS_STP__2_M: Int?, // Personen in schulischer Ausbildung nach Klassenstufe "Klasse 5 bis 9 bzw. 10" männlich (Anzahl)
    val SCHUELER_KLSS_STP__2_W: Int?, // Personen in schulischer Ausbildung nach Klassenstufe "Klasse 5 bis 9 bzw. 10" weiblich (Anzahl)
    val SCHUELER_KLSS_STP__3: Int?, // Personen in schulischer Ausbildung nach Klassenstufe "Gymnasiale Oberstufe (ab 10./11.Klasse)" insgesamt (Anzahl)
    val SCHUELER_KLSS_STP__3_M: Int?, // Personen in schulischer Ausbildung nach Klassenstufe "Gymnasiale Oberstufe (ab 10./11.Klasse)" männlich (Anzahl)
    val SCHUELER_KLSS_STP__3_W: Int?, // Personen in schulischer Ausbildung nach Klassenstufe "Gymnasiale Oberstufe (ab 10./11.Klasse)" weiblich (Anzahl)



) {
    val arsKey by lazy { ARSKey(_RS, Name) }

    fun getSchoolTarget(schoolLevel: SchoolLevel): Int? {
        return when (schoolLevel) {
            SchoolLevel.TOTAL -> SCHUELER_KLSS_STP
            SchoolLevel.ELEMENTARY -> SCHUELER_KLSS_STP__1
            SchoolLevel.MIDDLESCHOOL -> SCHUELER_KLSS_STP__2
            SchoolLevel.HIGHSCHOOL -> SCHUELER_KLSS_STP__3
        }
    }

    fun getSchoolTarget(schoolLevel: SchoolLevel, sex: Sex): Int? {
        return when (sex) {
            FEMALE -> getFemaleSchoolTarget(schoolLevel)
            MALE -> getMaleSchoolTarget(schoolLevel)
            else -> throw IllegalArgumentException("Census cannot return for this $sex")
        }
    }

    fun getFemaleSchoolTarget(schoolLevel: SchoolLevel): Int? {
        return when( schoolLevel) {
            SchoolLevel.TOTAL -> SCHUELER_KLSS_STP__W
            SchoolLevel.ELEMENTARY -> SCHUELER_KLSS_STP__1_W
            SchoolLevel.MIDDLESCHOOL -> SCHUELER_KLSS_STP__2_W
            SchoolLevel.HIGHSCHOOL -> SCHUELER_KLSS_STP__3_W
        }
    }
    fun getMaleSchoolTarget(schoolLevel: SchoolLevel): Int? {
        return when( schoolLevel) {
            SchoolLevel.TOTAL -> SCHUELER_KLSS_STP__M
            SchoolLevel.ELEMENTARY -> SCHUELER_KLSS_STP__1_M
            SchoolLevel.MIDDLESCHOOL -> SCHUELER_KLSS_STP__2_M
            SchoolLevel.HIGHSCHOOL -> SCHUELER_KLSS_STP__3_M
        }
    }
}

enum class SchoolLevel(val ageRange: IntRange) {
    TOTAL(5..22),
    ELEMENTARY(6..9),
    MIDDLESCHOOL(10..15),
    HIGHSCHOOL(16..19);
    companion object {
        val SPECIFIC = setOf(ELEMENTARY, MIDDLESCHOOL, HIGHSCHOOL)
    }
}