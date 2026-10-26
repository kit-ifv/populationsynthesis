package edu.kit.ifv.populationsynthesis.rules.composer

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchyGraphFactory
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.measurement.MeasurementDefinition
import edu.kit.ifv.populationsynthesis.rules.measurement.NamedMeasurement
import edu.kit.ifv.populationsynthesis.rules.provider.MapRuleProvider
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider
import edu.kit.ifv.populationsynthesis.rules.toRuleSet
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.A
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.Area
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.B
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.C
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HierarchyComposerTest {
    private class Target

    private class RuleDefinition(val id: String) : MeasurementDefinition<Target> {
        override fun createNamedMeasurement(): NamedMeasurement<Target> {
            return NamedMeasurement.boolean(id) {
                false
            }
        }

        override fun generateDescription(): String {
            return id
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

            private val A1Rules = listOf(definition1.createNamedMeasurement().withTarget(1.0))
            private val A2Rules = listOf(definition2.createNamedMeasurement().withTarget(1.0))
            private val B1Rules = listOf(
                definition1.createNamedMeasurement().withTarget(2.0),
                definition3.createNamedMeasurement().withTarget(2.0)
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

            override val keys: Set<Area>
                get() = getAllRules().keys
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

            addRule(A.A1, RulesHelper.A.generate(1.0))
            addRule(A.A2, RulesHelper.A.generate(2.0))
            addRule(A.A3, RulesHelper.A.generate(3.0))
            addRule(B.B2, RulesHelper.A.generate(10.0))


        }.withHierarchy(graph)

        val rules = provider.getComposedRules(C.C1)
        assertTrue(RulesHelper.A.text in rules)
        assertEquals(13.0, rules[RulesHelper.A.text]!!.target)

    }

    @Test
    fun uncoveredRule() {
        val provider = MapRuleProvider<Area, Any>().apply {
            addRule(A.A1, RulesHelper.B.generate(1.0))
            addRule(A.A2, RulesHelper.B.generate(2.0))
            addRule(A.A3, RulesHelper.B.generate(3.0))
        }.withHierarchy(graph)
        val rules = provider.getComposedRules(C.C1)
        assertTrue(RulesHelper.B.text in rules)
        assertEquals(6.0, rules[RulesHelper.B.text]!!.target)
    }

    @Test
    fun bestCoverage() {
        val provider = ruleCProvider().apply { addRule(C.C1, RulesHelper.C.generate(100.0)) }.withHierarchy(graph)
        val rules = provider.getComposedRules(C.C1)
        assertTrue(RulesHelper.C.text in rules)
        assertEquals(8.5, rules[RulesHelper.C.text]!!.target)
    }

    @Test
    fun differentOutputOnRequest() {
        val provider = ruleCProvider().withHierarchy(graph)
        val rules = provider.getComposedRules(C.C1)
        assertTrue(RulesHelper.C.text in rules)
        assertEquals(8.5, rules[RulesHelper.C.text]!!.target)
    }

    private fun ruleCProvider(): MapRuleProvider<Area, Any> {
        return MapRuleProvider<Area, Any>().apply {
            addRule(A.A1, RulesHelper.C.generate(1.0))
            addRule(A.A2, RulesHelper.C.generate(2.0))
            addRule(A.A3, RulesHelper.C.generate(3.0))
            addRule(A.A4, RulesHelper.C.generate(2.5))
            addRule(B.B1, RulesHelper.C.generate(10.0))
            addRule(B.B2, RulesHelper.C.generate(20.0))
        }

    }

}