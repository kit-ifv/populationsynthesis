package examples.threelayerscenario

internal interface Area

class A private constructor(val code: Int): Area{
    override fun toString() = "A($code)"
    companion object {
        val A1 = A(1)
        val A2 = A(2)
        val A3 = A(3)
        val A4 = A(4)
    }
}
class B private constructor(val code: Int): Area {
    override fun toString() = "B($code)"
    companion object {
        val B1 = B(1)
        val B2 = B(2)
    }
}
class C private constructor(val code: Int): Area {
    override fun toString() = "C($code)"
    companion object {
        val C1 = C(1)
    }
}