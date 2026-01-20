package edu.kit.ifv.populationsynthesis.rules.composer

import edu.kit.ifv.populationsynthesis.hierarchy.MutableHierarchyGraph
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.contribution.ContributionDefinition
import edu.kit.ifv.populationsynthesis.rules.contribution.NamedContribution
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider
import examples.threelayerscenario.A
import examples.threelayerscenario.Area
import examples.threelayerscenario.B
import examples.threelayerscenario.C
import examples.threelayerscenario.SeedElement
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class HierarchyComposerTest {
    private class Target

    private class RuleDefinition(val id: String): ContributionDefinition<Target> {
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
                return when(target) {
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
        assertEquals(2, output.size)
    }

}