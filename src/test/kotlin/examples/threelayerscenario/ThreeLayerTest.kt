package examples.threelayerscenario

import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu.NakedIPU
import edu.kit.ifv.populationsynthesis.hierarchy.groupByHighestAncestor
import kotlin.test.Test
import kotlin.test.assertEquals

class ThreeLayerTest {
    @Test
    fun example() {
        val hierarchicProvider = ABCRuleProvider.withHierarchy(ABCGraph)

        val ipu = NakedIPU(
            hierarchicProvider,
            SeedElement.all,
        )

        val output = ipu.synthesizeAll()

        println(output)
    }
    @Test
    fun properCompostion() {
        val hierarchicProvider = ABCRuleProvider.withHierarchy(ABCGraph)
        val rules = hierarchicProvider.getEffectiveRules(C.C1)
        val logicAssignment = rules.associate{it.logic.identifier to it.target}
        assertEquals(logicAssignment["YesDescriptor(A)"]?.toInt(), 1 + 4 +5 + 7)
        assertEquals(6, rules.size)
    }

    @Test
    fun borkedRuleDefinitions() {

    }
    @Test
    fun hierarchyResults() {
        val output = ABCGraph.groupByHighestAncestor(A.A1, A.A2)
        assertEquals(output.keys.first(), B.B1)
        assertEquals(output.values.first(), setOf(A.A1, A.A2, B.B1))
    }
}