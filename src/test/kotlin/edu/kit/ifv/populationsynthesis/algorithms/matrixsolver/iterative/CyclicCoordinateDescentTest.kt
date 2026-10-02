package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.sum
import edu.kit.ifv.populationsynthesis.datastructures.linalg.toList
import edu.kit.ifv.populationsynthesis.datastructures.linalg.toSparseScalableVector
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class CyclicCoordinateDescentTest {
    @Test
    fun testScenario() {
        val ccd = Solvers.CCD
        val a = Vector(1, 1).toSparseScalableVector()
        val b = Vector(1, 0).toSparseScalableVector()
        val c = Vector(1, -1).toSparseScalableVector()

        val vectors = listOf(a, b, c)
        val o1 = RuleObserver.fromIndex(0, 8.0, vectors)
        val o2 = RuleObserver.fromIndex(1, 0.0, vectors)

        val state = ccd.initialize(vectors, listOf(o1, o2))
        assertTrue(state.residual.contentEquals(doubleArrayOf(5.0, .0)))
        assertEquals(2.5, state.leastSquaresScalar(state.vectors[0]))
        assertEquals(5.0, state.leastSquaresScalar(state.vectors[1]))
        assertEquals(2.5, state.leastSquaresScalar(state.vectors[2]))
        // Manually pretend that the first vector got updated
        state.vectors[0].scalar += 2.5

        assertTrue(state.vectors.sum().contentEquals(Vector(5.5, 2.5)) )
        assertEquals(listOf(2.5, -2.5), state.residual.toList())
        assertTrue(state.residual.contentEquals(doubleArrayOf(2.5, -2.5)))
        assertEquals(.0, state.leastSquaresScalar(state.vectors.first()))
        assertEquals(2.5, state.leastSquaresScalar(state.vectors[1]))
        assertEquals(2.5, state.leastSquaresScalar(state.vectors[2]))


    }
}