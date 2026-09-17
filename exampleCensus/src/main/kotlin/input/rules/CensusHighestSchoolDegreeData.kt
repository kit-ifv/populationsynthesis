package edu.kit.ifv.populationsynthesis.input.rules

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import edu.kit.ifv.populationsynthesis.domain.population.Sex
import java.nio.file.Path

@JsonIgnoreProperties(ignoreUnknown = true)
data class CensusHighestSchoolDegreeData(
    val Berichtszeitpunkt: String,
    val _RS: String,
    val Name: String,
    val Regionalebene: String,
    val SCHULABS_STP: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Insgesamt" insgesamt (Anzahl)
    val SCHULABS_STP__M: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Insgesamt" männlich (Anzahl)
    val SCHULABS_STP__W: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Insgesamt" weiblich (Anzahl)
    val SCHULABS_STP__1: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Noch in schulischer Ausbildung" insgesamt (Anzahl)
    val SCHULABS_STP__1_M: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Noch in schulischer Ausbildung" männlich (Anzahl)
    val SCHULABS_STP__1_W: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Noch in schulischer Ausbildung" weiblich (Anzahl)
    val SCHULABS_STP__2: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Mit allgemeinbildendem Schulabschluss" insgesamt (Anzahl)
    val SCHULABS_STP__2_M: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Mit allgemeinbildendem Schulabschluss" männlich (Anzahl)
    val SCHULABS_STP__2_W: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Mit allgemeinbildendem Schulabschluss" weiblich (Anzahl)
    val SCHULABS_STP__21: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Haupt-/Volksschulabschluss" insgesamt (Anzahl)
    val SCHULABS_STP__21_M: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Haupt-/Volksschulabschluss" männlich (Anzahl)
    val SCHULABS_STP__21_W: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Haupt-/Volksschulabschluss" weiblich (Anzahl)
    val SCHULABS_STP__22: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Abschluss der Polytechnischen Oberschule" insgesamt (Anzahl)
    val SCHULABS_STP__22_M: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Abschluss der Polytechnischen Oberschule" männlich (Anzahl)
    val SCHULABS_STP__22_W: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Abschluss der Polytechnischen Oberschule" weiblich (Anzahl)
    val SCHULABS_STP__23: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Realschulabschluss, Mittlere Reife oder gleichwertiger Abschluss" insgesamt (Anzahl)
    val SCHULABS_STP__23_M: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Realschulabschluss, Mittlere Reife oder gleichwertiger Abschluss" männlich (Anzahl)
    val SCHULABS_STP__23_W: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Realschulabschluss, Mittlere Reife oder gleichwertiger Abschluss" weiblich (Anzahl)
    val SCHULABS_STP__24: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Fachhochschul- oder Hochschulreife (Abitur)" insgesamt (Anzahl)
    val SCHULABS_STP__24_M: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Fachhochschul- oder Hochschulreife (Abitur)" männlich (Anzahl)
    val SCHULABS_STP__24_W: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Fachhochschul- oder Hochschulreife (Abitur)" weiblich (Anzahl)
    val SCHULABS_STP__3: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Ohne allgemeinbildenden Schulabschluss" insgesamt (Anzahl)
    val SCHULABS_STP__3_M: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Ohne allgemeinbildenden Schulabschluss" männlich (Anzahl)
    val SCHULABS_STP__3_W: Int?, // Personen ab 15 Jahren nach höchstem Schulabschluss "Ohne allgemeinbildenden Schulabschluss" weiblich (Anzahl)

) {
    val arsKey by lazy { ARSKey(_RS, Name) }
    
    fun degreeWithSex(degree: SchoolDegree, sex: Sex): Int? {
        return when (sex) {
            Sex.FEMALE -> {
                when(degree) {
                    SchoolDegree.HAUPTSCHULE -> SCHULABS_STP__21_W
                    SchoolDegree.REALSCHULE -> SCHULABS_STP__22_W?.plus(SCHULABS_STP__23_W?:0)
                    SchoolDegree.ABITUR -> SCHULABS_STP__24_W
                }
            }
            Sex.MALE -> {
                when(degree) {
                    SchoolDegree.HAUPTSCHULE -> SCHULABS_STP__21_M
                    SchoolDegree.REALSCHULE -> SCHULABS_STP__22_M?.plus(SCHULABS_STP__23_M?:0)
                    SchoolDegree.ABITUR -> SCHULABS_STP__24_M
                }
            }
            else -> {
                null
            }
        }
    }

}

enum class SchoolDegree(val acceptedCodes: Set<Int>) {
    HAUPTSCHULE(setOf(2)),
    REALSCHULE(setOf(3)),
    ABITUR(setOf(4, 5));
}
