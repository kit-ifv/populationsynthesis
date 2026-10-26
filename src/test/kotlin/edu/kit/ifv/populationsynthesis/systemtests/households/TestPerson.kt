package edu.kit.ifv.populationsynthesis.systemtests.households

interface TestPerson {
    val age: Int
    val sex: Boolean get() = true
}