package examples.households

interface TestPerson {
    val age: Int
    val sex: Boolean get() = true
}