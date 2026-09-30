package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.sum


interface MutableSolverState: SolverState {
    override val vectors: List<ScalableVector>
    fun normalize() {
        val targetLength = target.length()
        val currentLength = vectors.sum().length()

        val scalingFactor = targetLength / currentLength
        vectors.forEach { vector ->
            vector.scalar *= scalingFactor
        }
    }
}
