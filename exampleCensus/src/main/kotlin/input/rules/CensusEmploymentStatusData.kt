package edu.kit.ifv.populationsynthesis.input.rules

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import edu.kit.ifv.populationsynthesis.domain.population.Sex
import edu.kit.ifv.populationsynthesis.rules.EmploymentStatus

@JsonIgnoreProperties(ignoreUnknown = true)
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

) {

    val arsKey by lazy { ARSKey(_RS, Name) }
    fun getGenderedEmploymentStatus(sex: Sex, employmentStatus: EmploymentStatus):Int? {
        return when (sex) {
            Sex.MALE -> getMaleEmploymentStatus(employmentStatus)
            Sex.FEMALE -> getFemaleEmploymentStatus(employmentStatus)
            else -> throw IllegalArgumentException("Cannot use thta")
        }
    }

    fun getMaleEmploymentStatus(employmentStatus: EmploymentStatus): Int? {
        return when (employmentStatus) {
            EmploymentStatus.ANY -> ERWERBSTAT_KURZ_STP__M
            EmploymentStatus.EMPLOYED -> ERWERBSTAT_KURZ_STP__11_M
            EmploymentStatus.UNEMPLOYED -> ERWERBSTAT_KURZ_STP__12_M
            EmploymentStatus.NOT_WORKING -> ERWERBSTAT_KURZ_STP__2_M
        }
    }

    fun getFemaleEmploymentStatus(employmentStatus: EmploymentStatus): Int? {
        return when (employmentStatus) {
            EmploymentStatus.ANY -> ERWERBSTAT_KURZ_STP__W
            EmploymentStatus.EMPLOYED -> ERWERBSTAT_KURZ_STP__11_W
            EmploymentStatus.UNEMPLOYED -> ERWERBSTAT_KURZ_STP__12_W
            EmploymentStatus.NOT_WORKING -> ERWERBSTAT_KURZ_STP__2_W
        }
    }
}