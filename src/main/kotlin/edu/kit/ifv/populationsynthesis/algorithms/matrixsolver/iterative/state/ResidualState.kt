package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.SolverState
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector

interface ResidualState : SolverState {
    override val vectors: List<ResidualSolverState.ProtectedScalableVector>
    val residual: Vector
    fun leastSquaresScalar(vector: ResidualSolverState.ProtectedScalableVector): Double {
        val denominator = vector.baseNormSquared()
        return (vector.baseDot(residual) / denominator)
    }
}