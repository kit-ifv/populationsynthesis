package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.toList
import edu.kit.ifv.populationsynthesis.datastructures.linalg.toSparseScalableVector
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class IterativeProportionalUpdatingTest {
    @Test
    fun ipuTest() {
        val ipu = Solvers.iterativeProportionalUpdate {
            stateFactory = SolverStateFactory.ipuState
        }
        val a = Vector(1, 1).toSparseScalableVector()
        val b = Vector(1, 3).toSparseScalableVector()

        val vectors = listOf(a, b)
        val o1 = RuleObserver.fromIndex(0, 4.0, vectors)
        val o2 = RuleObserver.fromIndex(1, 6.0, vectors)
        val state = ipu.initialize(vectors, listOf(o1, o2))

        assertContentEquals(listOf(4.0, 6.0), state.target.toList())
        state.observers[0].optimize()
        assertEquals(2.0, a.scalar)
        assertEquals(2.0, b.scalar)
        state.observers[1].optimize()
        assertEquals(1.5, a.scalar)
        assertEquals(1.5, b.scalar)
    }
}