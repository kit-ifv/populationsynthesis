package examples

import examples.households.AgeRules
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class ContributionTest {
    @Test
    fun test() {
        val ruleSpawner = AgeRules(10, 18, 66)
        val ruleContributions = ruleSpawner.generateAllDescriptions()
        assertEquals(ruleContributions.size, 4)

        val otherSpawner = AgeRules(10, 18, 66)
        val otherContributions = otherSpawner.generateAllDescriptions()
        assertEquals(ruleContributions[0], otherContributions[0])
        assertEquals(ruleContributions[1], otherContributions[1])
        assertEquals(ruleContributions[2], otherContributions[2])
        assertEquals(ruleContributions[3], otherContributions[3])

        val t = ruleContributions.groupBy { it.origin }

        println(t)
    }
}