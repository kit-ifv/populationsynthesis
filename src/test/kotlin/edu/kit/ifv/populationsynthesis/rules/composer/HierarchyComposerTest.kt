package edu.kit.ifv.populationsynthesis.rules.composer

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchyGraphFactory
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.contribution.ContributionDefinition
import edu.kit.ifv.populationsynthesis.rules.contribution.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.contribution.NamedContribution
import edu.kit.ifv.populationsynthesis.rules.provider.MapRuleProvider
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider
import edu.kit.ifv.populationsynthesis.rules.toRuleSet
import examples.threelayerscenario.A
import examples.threelayerscenario.Area
import examples.threelayerscenario.B
import examples.threelayerscenario.C
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HierarchyComposerTest {
    private class Target

    private class RuleDefinition(val id: String) : ContributionDefinition<Target> {
        override fun createNamedContribution(): NamedContribution<Target> {
            return NamedContribution.boolean(id) {
                false
            }
        }
    }

    @Test
    fun multilevelComposition() {

        val graph = HierarchyGraphFactory.asForest {
            addRelationship(A.A1, B.B1)
            addRelationship(A.A2, B.B1)
            addRelationship(B.B1, C.C1)
        }

        val composer = HierarchyComposer<Area, Target>(graph)
        val definition1 = RuleDefinition("1")
        val definition2 = RuleDefinition("2")
        val definition3 = RuleDefinition("3")
        val logicProvider: RuleProvider<Area, Target> = object : RuleProvider<Area, Target> {

            private val A1Rules = listOf(definition1.createNamedContribution().withTarget(1.0))
            private val A2Rules = listOf(definition2.createNamedContribution().withTarget(1.0))
            private val B1Rules = listOf(
                definition1.createNamedContribution().withTarget(2.0),
                definition3.createNamedContribution().withTarget(2.0)
            )

            override fun getRules(target: Area): RuleSet<Target> {
                return when (target) {
                    A.A1 -> A1Rules
                    A.A2 -> A2Rules
                    B.B1 -> B1Rules
                    else -> emptyList()
                }.toRuleSet()
            }

            override fun getAllRules(): Map<Area, RuleSet<Target>> {
                return mapOf(A.A1 to getRules(A.A1), A.A2 to getRules(A.A2), B.B1 to getRules(B.B1))
            }

            override fun get(
                target: Area,
                logicIdentifier: LogicIdentifier
            ): Rule<Target>? {
                return when (target) {
                    A.A1 -> A1Rules.find { it.logic.identifier == logicIdentifier }
                    A.A2 -> A2Rules.find { it.logic.identifier == logicIdentifier }
                    B.B1 -> B1Rules.find { it.logic.identifier == logicIdentifier }
                    else -> null
                }
            }

        }
        val output = composer.compose(C.C1, logicProvider)
        assertEquals(3, output.size)
    }

    private val graph = HierarchyGraphFactory.asForest {
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
        val provider = ruleCProvider().apply { addRule(C.C1, HelperRules.C.generate(100.0)) }.withHierarchy(graph)
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