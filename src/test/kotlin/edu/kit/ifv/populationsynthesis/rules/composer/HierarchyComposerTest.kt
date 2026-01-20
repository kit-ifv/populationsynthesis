package edu.kit.ifv.populationsynthesis.rules.composer

import edu.kit.ifv.populationsynthesis.hierarchy.MutableHierarchyGraph
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.contribution.ContributionDefinition
import edu.kit.ifv.populationsynthesis.rules.contribution.NamedContribution
import edu.kit.ifv.populationsynthesis.rules.provider.MapRuleProvider
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider
import examples.threelayerscenario.A
import examples.threelayerscenario.Area
import examples.threelayerscenario.B
import examples.threelayerscenario.C
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HierarchyComposerTest {
    private class Target

    private class HelperRules(val text: String) {

        fun generate(target: Double): Rule<Any> {
            return Rule(target, NamedContribution.boolean(text) { true })
        }

        companion object {
            val A = HelperRules("Hello")
            val B = HelperRules("World")
            val C = HelperRules("Else")
        }
    }

    private class RuleDefinition(val id: String) : ContributionDefinition<Target> {
        override fun createNamedContribution(): NamedContribution<Target> {
            return NamedContribution.boolean(id) {
                false
            }
        }
    }

    @Test
    fun multilevelComposition() {

        val graph = MutableHierarchyGraph<Area>().apply {
            addRelationship(A.A1, B.B1)
            addRelationship(A.A2, B.B1)
            addRelationship(B.B1, C.C1)
        }

        val composer = HierarchyComposer<Area, Target>(graph)
        val definition1 = RuleDefinition("1")
        val definition2 = RuleDefinition("2")
        val definition3 = RuleDefinition("3")
        val logicProvider: RuleProvider<Area, Target> = object : RuleProvider<Area, Target> {
            override fun getRules(target: Area): Collection<Rule<Target>> {
                return when (target) {
                    A.A1 -> listOf(definition1).map { it.createNamedContribution().withTarget(1.0) }
                    A.A2 -> listOf(definition2).map { it.createNamedContribution().withTarget(1.0) }
                    B.B1 -> listOf(definition1, definition3).map { it.createNamedContribution().withTarget(2.0) }
                    else -> emptyList()
                }
            }

            override fun getAllRules(): Map<Area, Collection<Rule<Target>>> {
                TODO("Not yet implemented")
            }

        }
        val output = composer.compose(C.C1, logicProvider::getRules)
        assertEquals(3, output.size)
    }

    private val graph = MutableHierarchyGraph<Area>().apply {
        addRelationship(A.A1, B.B1)
        addRelationship(A.A2, B.B1)
        addRelationship(A.A3, B.B2)
        addRelationship(A.A4, B.B2)
        addRelationship(B.B1, C.C1)
        addRelationship(B.B2, C.C1)
    }

    @Test
    fun parentNeedNotDefineRule() {


        val provider = MapRuleProvider<Area, Any>().apply {

            addRule(A.A1, HelperRules.A.generate(1.0))
            addRule(A.A2, HelperRules.A.generate(2.0))
            addRule(A.A3, HelperRules.A.generate(3.0))
            addRule(B.B2, HelperRules.A.generate(10.0))


        }.withHierarchy(graph)

        val rules = provider.getComposedRules(C.C1)
        assertTrue(HelperRules.A.text in rules)
        assertEquals(rules[HelperRules.A.text]!!.target, 13.0)

    }

    @Test
    fun uncoveredRule() {
        val provider = MapRuleProvider<Area, Any>().apply {
            addRule(A.A1, HelperRules.B.generate(1.0))
            addRule(A.A2, HelperRules.B.generate(2.0))
            addRule(A.A3, HelperRules.B.generate(3.0))
        }.withHierarchy(graph)
        val rules = provider.getComposedRules(C.C1)
        assertTrue(HelperRules.B.text in rules)
        assertEquals(rules[HelperRules.B.text]!!.target, 6.0)
    }

    @Test
    fun bestCoverage() {
        val provider = ruleCProvider().apply { addRule(C.C1, HelperRules.C.generate(100.0))}.withHierarchy(graph)
        val rules = provider.getComposedRules(C.C1)
        assertTrue(HelperRules.C.text in rules)
        assertEquals(rules[HelperRules.C.text]!!.target, 8.5)
    }

    @Test
    fun differentOutputOnRequest() {
        val provider = ruleCProvider().withHierarchy(graph)
        val rules = provider.getComposedRules(C.C1)
        assertTrue(HelperRules.C.text in rules)
        assertEquals(rules[HelperRules.C.text]!!.target, 8.5)
    }
    private fun ruleCProvider(): MapRuleProvider<Area, Any> {
        return MapRuleProvider<Area, Any>().apply {
            addRule(A.A1, HelperRules.C.generate(1.0))
            addRule(A.A2, HelperRules.C.generate(2.0))
            addRule(A.A3, HelperRules.C.generate(3.0))
            addRule(A.A4, HelperRules.C.generate(2.5))
            addRule(B.B1, HelperRules.C.generate(10.0))
            addRule(B.B2, HelperRules.C.generate(20.0))
        }

    }

}