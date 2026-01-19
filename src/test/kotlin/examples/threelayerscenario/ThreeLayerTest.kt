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
        val cRules = hierarchicProvider.getEffectiveRules(C.C1)
        assertEquals(6, cRules.size)
        assertEquals(cRules.getValue("YesDescriptor(A)").target.toInt(), 1 + 4 + 5 + 7)

        val bRules = hierarchicProvider.getEffectiveRules(B.B1)
        assertEquals(4, bRules.size)
    }

    @Test
    fun hierarchyResults() {
        val output = ABCGraph.groupByHighestAncestor(A.A1, A.A2)
        assertEquals(output.keys.first(), C.C1)
        assertEquals(output.values.first(), setOf(A.A1, A.A2, B.B1, C.C1))
    }

    @Test
    fun forestResults() {
        val output = ABCGraph.groupByHighestAncestor(A.A1, A.A5)
        assertEquals(output.size, 2)
        assertEquals(output[C.C1]!!, setOf(A.A1, B.B1, C.C1))
        assertEquals(output[C.C2]!!, setOf(A.A5, B.B3, C.C2))
    }
}