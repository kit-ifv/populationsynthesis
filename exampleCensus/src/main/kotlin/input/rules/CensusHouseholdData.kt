package edu.kit.ifv.populationsynthesis.input.rules

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import edu.kit.ifv.populationsynthesis.domain.area.ARSKey


@JsonIgnoreProperties(ignoreUnknown = true)
data class CensusHouseholdData(

    val _RS: String,
    val Name: String,
    val Reg_Ebene: String,
    @JsonProperty("0_Insgesamt_")
    val Insgesamt_val: Int?, // Haushalte insgesamt (Anzahl)
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

    fun getHouseholdSizeTarget(target: Int): Int? {
        return when (target) {

            1 -> HH_SIZE_NAT__1
            2 -> HH_SIZE_NAT__2
            3 -> HH_SIZE_NAT__3
            4 -> HH_SIZE_NAT__4
            5 -> HH_SIZE_NAT__5
            6 -> HH_SIZE_NAT__6
            else -> throw NoSuchElementException("There is no household size for $target")
        }
    }

    fun getHouseholdSizesGreaterEqualTo(startingAt: Int): Int? {
        return (startingAt..6).fold(null) { acc, i ->
            acc + getHouseholdSizeTarget(i)

        }
    }

    /**
     * Counting nullable values takes a bit of effort. For [getHouseholdSizesGreaterEqualTo]
     */
    private operator fun Int?.plus(other: Int?): Int? {
        return this?.plus(other ?: 0) ?: other
    }
}

