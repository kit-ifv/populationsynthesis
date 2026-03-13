package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.rules.measurement.NamedMeasurement
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class MapRuleProviderTest {
    private interface A
    private class B : A
    private data class Reg(val id: Boolean) {}
    private val R = Reg(true)

    private val ARule = NamedMeasurement.numeric<A>("This is A") {
        1.0
    }.withTarget(10.0)
    private val BRule = NamedMeasurement.boolean<B>("This is B") {
        true
    }.withTarget(10.0)
    @Test
    fun properLoadageOfOtherRuleProvider() {
        val initialRuleProvider = MapRuleProvider<Reg, B>()
        val secondRuleProvider = MapRuleProvider<Reg, A>()
        secondRuleProvider.addRule(R, ARule)
        initialRuleProvider.loadFromOtherRuleProvider(secondRuleProvider) {
            it.key.id
        }
        val rules = initialRuleProvider.getRules(R)
        assertEquals(1, rules.size)
        assertEquals(0, initialRuleProvider.getRules(Reg(false)).size)
    }
    @Test
    fun canSetWeakerSets() {
        val initialRuleProvider = MapRuleProvider<Reg, B>()
        initialRuleProvider[R] = listOf(ARule, BRule)
    }
}