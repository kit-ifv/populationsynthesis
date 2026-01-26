package edu.kit.ifv.populationsynthesis.hierarchy

import examples.threelayerscenario.A
import examples.threelayerscenario.Area
import examples.threelayerscenario.B
import examples.threelayerscenario.C
import org.junit.jupiter.api.Assertions.*
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
        assertEquals(output.size, 1)
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
        assertEquals(output.size, 1)
        assertTrue(C.C1 !in output)
    }
}