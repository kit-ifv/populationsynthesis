package edu.kit.ifv.populationsynthesis.datasource.input

data class ARSKey(
    val arsKey: String,
    val description: String = "",
    val level: AreaHierarchy = AreaHierarchy.fromString(arsKey),
) {
    override fun equals(other: Any?): Boolean {
        if (other !is ARSKey) return false
        return arsKey == other.arsKey && level == other.level
    }

    override fun hashCode(): Int {
        var result = arsKey.hashCode()
        result = 31 * result + level.hashCode()
        return result
    }

    operator fun contains(value: ARSKey): Boolean {
        if (level == AreaHierarchy.DEUTSCHLAND) return true // Germany contains every key that is in germany
        if (value == this) return true
        return level.ordinal < value.level.ordinal && value.arsKey.startsWith(this.arsKey)
    }


    operator fun plus(string: String) = plus(string to "")
    operator fun plus(input: Pair<String, String>): ARSKey {
        return ARSKey(
            arsKey = arsKey + input.first,
            description = description + input.second,
            level = level + 1
        )
    }

    fun simplify(level: AreaHierarchy): ARSKey {
        if (level == AreaHierarchy.ORT || level == AreaHierarchy.ORTSTEIL) {
            throw UnsupportedOperationException("ORT and ORTSTEIL are NOT PART of the defined key set and thus their amount of digits is project specific.")
        }
        val key1 = arsKey.take(level.digits)
        return ARSKey(key1, key1)
    }

    companion object {
        val MUNICH =
            ARSKey(
                arsKey = "091620000000",
                description = "München, Landeshauptstadt",
                level = AreaHierarchy.GEMEINDE
            )
        val OBERBAYERN = ARSKey(
            arsKey = "091",
            description = "Oberbayern Regierungsbezirk",
        )
        val DEUTSCHLAND = ARSKey(
            arsKey = "00",
            "DEUTSCHLAND",
            level = AreaHierarchy.DEUTSCHLAND
        )
        val MARNE_NORDSEE = ARSKey(
            arsKey = "010515166",
            "MARNE_NORDSEE",
            level = AreaHierarchy.GEMEINDEVERBAND
        )

        val NORDRHEIN_WESTFALEN = ARSKey(
            arsKey = "05",
            description = "NORDRHEIN_WESTFALEN",
            level = AreaHierarchy.BUNDESLAND
        )

        val HERZOGTUM_LAUENBURG = ARSKey(
            arsKey = "01053",
            description = "HERZOGTUM_LAUENBURG",
            level = AreaHierarchy.LANDKREIS
        )


        val DIEKHUSEN_FAHRSTEDT = ARSKey(arsKey= "010515166021",description = "Diekhusen_Fahrstedt")
        val FRIEDRICHSKOOG = ARSKey(arsKey= "010515166034",description = "Friedrichskoog")
        val HELSE = ARSKey(arsKey= "010515166046",description = "Helse")
        val KAISER_WILHELM_KOOG = ARSKey(arsKey= "010515166057",description = "Kaiser_Wilhelm_Koog")
        val KRONPRINZENKOOG = ARSKey(arsKey= "010515166062",description = "Kronprinzenkoog")
        val MARNE_STADT = ARSKey(arsKey= "010515166072",description = "Marne_Stadt")
        val MARNERDEICH = ARSKey(arsKey= "010515166073",description = "Marnerdeich")
        val NEUFELD = ARSKey(arsKey= "010515166076",description = "Neufeld")
        val NEUFELDERKOOG = ARSKey(arsKey= "010515166077",description = "Neufelderkoog")
        val RAMHUSEN = ARSKey(arsKey= "010515166090",description = "Ramhusen")
        val SCHMEDESWURTH = ARSKey(arsKey= "010515166103",description = "Schmedeswurth")
        val TRENNEWURTH = ARSKey(arsKey= "010515166118",description = "Trennewurth")
        val VOLSEMENHUSEN = ARSKey(arsKey= "010515166119",description = "Volsemenhusen")


    }


}

