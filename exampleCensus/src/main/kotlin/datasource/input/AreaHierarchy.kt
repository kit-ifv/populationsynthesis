package edu.kit.ifv.populationsynthesis.datasource.input

enum class AreaHierarchy(val digits: Int):  Comparable<AreaHierarchy> {
    DEUTSCHLAND(0), BUNDESLAND(2), REGIERUNGSBEZIRK(3), LANDKREIS(5), GEMEINDEVERBAND(9), GEMEINDE(12), ORT(14), ORTSTEIL(
        15
    );

    operator fun plus(int: Int): AreaHierarchy {
        return entries[ordinal + int]
    }

    companion object {


        fun fromString(text: String, fallback: AreaHierarchy? = null): AreaHierarchy {
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