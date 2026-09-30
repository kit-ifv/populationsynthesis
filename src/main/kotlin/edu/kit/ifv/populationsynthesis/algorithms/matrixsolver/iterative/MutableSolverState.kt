package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import com.google.ortools.constraintsolver.Solver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.toVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScaledVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.sum
import kotlin.time.TimeSource


interface MutableSolverState: SolverState {
    override val vectors: List<ScaledVector>
    fun normalize() {
        val targetLength = target.length()
        val currentLength = vectors.sum().length()

        val scalingFactor = targetLength / currentLength
        vectors.forEach { vector ->
            vector.scalar *= scalingFactor
        }
    }
}
