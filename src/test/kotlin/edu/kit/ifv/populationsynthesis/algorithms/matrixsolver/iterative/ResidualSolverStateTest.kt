package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state.ResidualSolverState
import edu.kit.ifv.populationsynthesis.datastructures.linalg.SparseScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.SparseVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class ResidualSolverStateTest {

    @Test
    fun residualIsProtected() {
        val oldReference = SparseScalableVector(1.0, SparseVector.fromValues(0, 1))
        val state = ResidualSolverState(
            Vector(2, 2), listOf(
                oldReference,
                SparseScalableVector(1.0, SparseVector.fromValues(1, 1))
            )
        )

        assertTrue(state.residual.contentEquals(doubleArrayOf(1.0, .0)))
        oldReference.scalar = 2.0
        assertTrue(state.residual.contentEquals(doubleArrayOf(1.0, .0)))
        val activeReference = state.vectors.first()
        activeReference.scalar = 2.0
        assertFalse(state.residual.contentEquals(doubleArrayOf(1.0, .0)))
        assertTrue(state.residual.contentEquals(doubleArrayOf(1.0, -1.0)))

    }

}