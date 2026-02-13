package edu.kit.ifv.populationsynthesis.domain.area

enum class AreaLevel(val digits: Int):  Comparable<AreaLevel> {
    DEUTSCHLAND(0), BUNDESLAND(2), REGIERUNGSBEZIRK(3), LANDKREIS(5), GEMEINDEVERBAND(9), GEMEINDE(12), ORT(14), ORTSTEIL(
        15
    );

    operator fun plus(int: Int): AreaLevel {
        return entries[ordinal + int]
    }

    companion object {


        fun fromString(text: String, fallback: AreaLevel? = null): AreaLevel {
            if (text == "00") return DEUTSCHLAND
            return when (text.length) {
                2 -> BUNDESLAND
                3 -> REGIERUNGSBEZIRK
                5 -> LANDKREIS
                9 -> GEMEINDEVERBAND
                12 -> GEMEINDE
                else -> fallback ?: throw NoSuchElementException("Cannot turn $text into an ARS Key")
            }
        }

    }
}