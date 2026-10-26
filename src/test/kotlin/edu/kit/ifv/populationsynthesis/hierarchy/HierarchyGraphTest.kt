package edu.kit.ifv.populationsynthesis.hierarchy

import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.A
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.Area
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.B
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.C
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HierarchyGraphTest {
    @Test
    fun properGuardUsage() {
        val graph = HierarchyGraphFactory.asDAG<Area>(guard = C.C1) {
            addRelationship(A.A1, B.B1)
            addRelationship(A.A1, B.B2)
            addRelationship(A.A2, B.B2)
            addRelationship(A.A2, B.B3)
        }

        val output = graph.groupByHighestAncestor(A.A1)
        assertEquals(1, output.size)
        assertTrue(C.C1 in output)
    }

    @Test
    fun guardIsAvoidedWhenNotNeeded() {
        val graph = HierarchyGraphFactory.asDAG<Area>(guard = C.C1) {
            addRelationship(A.A1, B.B1)
            addRelationship(A.A2, B.B1)
            addRelationship(A.A3, B.B2)
            addRelationship(A.A4, B.B2)
        }

        val output = graph.groupByHighestAncestor(A.A1)
        assertEquals(1, output.size)
        assertTrue(C.C1 !in output)
    }
}