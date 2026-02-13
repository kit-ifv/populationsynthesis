package edu.kit.ifv.populationsynthesis.input.rules

data class CensusEmploymentStatusData(
    val Berichtszeitpunkt: String,
    val _RS: String,
    val Name: String,
    val Regionalebene: String,
    val ERWERBSTAT_KURZ_STP: Int?, // Personen nach Erwerbsstatus "Insgesamt" insgesamt (Anzahl)
    val ERWERBSTAT_KURZ_STP__M: Int?, // Personen nach Erwerbsstatus "Insgesamt" männlich (Anzahl)
    val ERWERBSTAT_KURZ_STP__W: Int?, // Personen nach Erwerbsstatus "Insgesamt" weiblich (Anzahl)
    val ERWERBSTAT_KURZ_STP__1: Int?, // Personen nach Erwerbsstatus "Erwerbspersonen" insgesamt (Anzahl)
    val ERWERBSTAT_KURZ_STP__1_M: Int?, // Personen nach Erwerbsstatus "Erwerbspersonen" männlich (Anzahl)
    val ERWERBSTAT_KURZ_STP__1_W: Int?, // Personen nach Erwerbsstatus "Erwerbspersonen" weiblich (Anzahl)
    val ERWERBSTAT_KURZ_STP__11: Int?, // Personen nach Erwerbsstatus "Erwerbstätige" insgesamt (Anzahl)
    val ERWERBSTAT_KURZ_STP__11_M: Int?, // Personen nach Erwerbsstatus "Erwerbstätige" männlich (Anzahl)
    val ERWERBSTAT_KURZ_STP__11_W: Int?, // Personen nach Erwerbsstatus "Erwerbstätige" weiblich (Anzahl)
    val ERWERBSTAT_KURZ_STP__12: Int?, // Personen nach Erwerbsstatus "Erwerbslose" insgesamt (Anzahl)
    val ERWERBSTAT_KURZ_STP__12_M: Int?, // Personen nach Erwerbsstatus "Erwerbslose" männlich (Anzahl)
    val ERWERBSTAT_KURZ_STP__12_W: Int?, // Personen nach Erwerbsstatus "Erwerbslose" weiblich (Anzahl)
    val ERWERBSTAT_KURZ_STP__2: Int?, // Personen nach Erwerbsstatus "Nichterwerbspersonen" insgesamt (Anzahl)
    val ERWERBSTAT_KURZ_STP__2_M: Int?, // Personen nach Erwerbsstatus "Nichterwerbspersonen" männlich (Anzahl)
    val ERWERBSTAT_KURZ_STP__2_W: Int?, // Personen nach Erwerbsstatus "Nichterwerbspersonen" weiblich (Anzahl)

)