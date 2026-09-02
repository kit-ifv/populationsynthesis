package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.ipu

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.MatrixSolver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector

class CyclicIPU(private val maxIterations: Int = 1000) : MatrixSolver {
    override fun solve(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ) {

        repeat(maxIterations) {
            observers.forEach {
                it.optimize()
            }
        }
    }

}