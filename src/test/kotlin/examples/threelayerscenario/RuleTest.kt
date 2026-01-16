package examples.threelayerscenario

import kotlin.test.Test
import kotlin.test.assertEquals

class RuleTest {
    @Test

    fun testRuleEquality() {
        val a = HelpGenerator.A(5, 5).generateRules()
        val b = HelpGenerator.A(7, 7).generateRules()
        a.zip(b).forEach { (a, b) ->
            assertEquals(b.logic, a.logic)
        }
    }
}