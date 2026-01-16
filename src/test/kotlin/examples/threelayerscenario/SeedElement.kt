package examples.threelayerscenario

data class SeedElement(val c: Boolean, val b: Boolean, val a: Boolean) {
    constructor(code: Int) : this(code and 4 == 4, code and 2 == 2, code and 1 == 1)
    companion object {
        val all = (0..7).map { SeedElement(it) }
    }
}
