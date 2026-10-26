package edu.kit.ifv.populationsynthesis.input.rules

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.nio.file.Path

@JsonIgnoreProperties(ignoreUnknown = true)
data class CensusSchoolFormData(
    val Berichtszeitpunkt: String,
    val _RS: String,
    val Name: String,
    val Regionalebene: String,
    val SCHUELER_SCH_STP: Int?, //Personen in schulischer Ausbildung nach Schulform "Insgesamt" insgesamt (Anzahl)
    val SCHUELER_SCH_STP__M: Int?, //Personen in schulischer Ausbildung nach Schulform "Insgesamt" männlich (Anzahl)
    val SCHUELER_SCH_STP__W: Int?, //Personen in schulischer Ausbildung nach Schulform "Insgesamt" weiblich (Anzahl)
    val SCHUELER_SCH_STP__1: Int?, //Personen in schulischer Ausbildung nach Schulform "Grundschule" insgesamt (Anzahl)
    val SCHUELER_SCH_STP__1_M: Int?, //Personen in schulischer Ausbildung nach Schulform "Grundschule" männlich (Anzahl)
    val SCHUELER_SCH_STP__1_W: Int?, //Personen in schulischer Ausbildung nach Schulform "Grundschule" weiblich (Anzahl)
    val SCHUELER_SCH_STP__2: Int?, //Personen in schulischer Ausbildung nach Schulform "Förderschule" insgesamt (Anzahl)
    val SCHUELER_SCH_STP__2_M: Int?, //Personen in schulischer Ausbildung nach Schulform "Förderschule" männlich (Anzahl)
    val SCHUELER_SCH_STP__2_W: Int?, //Personen in schulischer Ausbildung nach Schulform "Förderschule" weiblich (Anzahl)
    val SCHUELER_SCH_STP__3: Int?, //Personen in schulischer Ausbildung nach Schulform "Schule mit mehreren Bildungsgängen" insgesamt (Anzahl)
    val SCHUELER_SCH_STP__3_M: Int?, //Personen in schulischer Ausbildung nach Schulform "Schule mit mehreren Bildungsgängen" männlich (Anzahl)
    val SCHUELER_SCH_STP__3_W: Int?, //Personen in schulischer Ausbildung nach Schulform "Schule mit mehreren Bildungsgängen" weiblich (Anzahl)
    val SCHUELER_SCH_STP__4: Int?, //Personen in schulischer Ausbildung nach Schulform "Hauptschule" insgesamt (Anzahl)
    val SCHUELER_SCH_STP__4_M: Int?, //Personen in schulischer Ausbildung nach Schulform "Hauptschule" männlich (Anzahl)
    val SCHUELER_SCH_STP__4_W: Int?, //Personen in schulischer Ausbildung nach Schulform "Hauptschule" weiblich (Anzahl)
    val SCHUELER_SCH_STP__5: Int?, //Personen in schulischer Ausbildung nach Schulform "Realschule" insgesamt (Anzahl)
    val SCHUELER_SCH_STP__5_M: Int?, //Personen in schulischer Ausbildung nach Schulform "Realschule" männlich (Anzahl)
    val SCHUELER_SCH_STP__5_W: Int?, //Personen in schulischer Ausbildung nach Schulform "Realschule" weiblich (Anzahl)
    val SCHUELER_SCH_STP__6: Int?, //Personen in schulischer Ausbildung nach Schulform "Gesamtschule, Waldorfschule" insgesamt (Anzahl)
    val SCHUELER_SCH_STP__6_M: Int?, //Personen in schulischer Ausbildung nach Schulform "Gesamtschule, Waldorfschule" männlich (Anzahl)
    val SCHUELER_SCH_STP__6_W: Int?, //Personen in schulischer Ausbildung nach Schulform "Gesamtschule, Waldorfschule" weiblich (Anzahl)
    val SCHUELER_SCH_STP__7: Int?, //Personen in schulischer Ausbildung nach Schulform "Gymnasium, Kolleg" insgesamt (Anzahl)
    val SCHUELER_SCH_STP__7_M: Int?, //Personen in schulischer Ausbildung nach Schulform "Gymnasium, Kolleg" männlich (Anzahl)
    val SCHUELER_SCH_STP__7_W: Int?, //Personen in schulischer Ausbildung nach Schulform "Gymnasium, Kolleg" weiblich (Anzahl)

)  {

}

