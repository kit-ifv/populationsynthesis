package edu.kit.ifv.populationsynthesis.algorithms.ipu

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector

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