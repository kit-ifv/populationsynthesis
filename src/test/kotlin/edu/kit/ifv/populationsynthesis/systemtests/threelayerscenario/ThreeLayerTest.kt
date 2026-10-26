package edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario

import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu.NakedIPU
import edu.kit.ifv.populationsynthesis.hierarchy.HierarchyGraphFactory
import edu.kit.ifv.populationsynthesis.hierarchy.groupByHighestAncestor
import edu.kit.ifv.populationsynthesis.hierarchy.levels
import edu.kit.ifv.populationsynthesis.rules.provider.MapRuleProvider
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


class ThreeLayerTest {

    @Test
    fun properCompostion() {
        val hierarchicProvider = ABCRuleProvider.withHierarchy(ABCGraph)
        val cRules = hierarchicProvider.getComposedRules(C.C1)
        assertEquals(6, cRules.size)
        assertEquals(1 + 4 + 5 + 7, cRules.getValue("YesDescriptor(A)").target.toInt())

        val bRules = hierarchicProvider.getComposedRules(B.B1)
        assertEquals(4, bRules.size)
    }

    @Test
    fun hierarchyResults() {
        val output = ABCGraph.groupByHighestAncestor(A.A1, A.A2)
        assertEquals(C.C1, output.keys.first())
        assertEquals(setOf(A.A1, A.A2, B.B1, C.C1), output.values.first())
    }

    @Test
    fun forestResults() {
        val output = ABCGraph.groupByHighestAncestor(A.A1, A.A5)
        assertEquals(2, output.size)
        assertEquals(setOf(A.A1, B.B1, C.C1), output[C.C1]!!)
        assertEquals(setOf(B.B3, A.A5, C.C2), output[C.C2]!!)
    }

    @Test
    fun disallowHierarchicDuplicates() {
        val graph = HierarchyGraphFactory.asForest {
            addRelationship(A.A1, B.B1)
        }
        val duplicatedRuleProvider = MapRuleProvider<Area, SeedElement>().apply {
            addRules(B.B1, HelpGenerator.B(2, 2))
            addRules(B.B1, HelpGenerator.A(2, 2)) // Oh no, I mistyped. Or maybe intent. Should not be added with A.A1
            addRules(A.A1, HelpGenerator.A(5, 5))
        }.withHierarchy(graph)

        val rules = duplicatedRuleProvider.getComposedRules(B.B1)
        assertEquals(4, rules.size)
        assertNotEquals(7, rules.getValue("YesDescriptor(A)").target.toInt())
    }

    @Test
    fun example() {
        val hierarchicProvider = ABCRuleProvider.withHierarchy(ABCGraph)

        val ipu = NakedIPU(
            hierarchicProvider,
            SeedElement.all,
        )
        assertDoesNotThrow {
            ipu.synthesizeAll()
        }

    }

    @Test
    fun levels() {
        val output = ABCGraph.levels(C.C1).toList()
        assertEquals(setOf(C.C1), output[0])
        assertEquals(setOf(B.B1, B.B2), output[1])
        assertEquals(setOf(A.A1, A.A2, A.A3, A.A4), output[2])
    }
}