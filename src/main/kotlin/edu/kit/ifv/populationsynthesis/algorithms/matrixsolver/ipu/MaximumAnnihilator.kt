package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.ipu

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.MatrixSolver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import kotlin.math.abs

class MaximumAnnihilator : MatrixSolver {
    override fun solve(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ) {

        val tracker = observers.toMutableList()
        repeat(1000) {
            val worstObserver = tracker.maxBy { abs(it.actual - it.expected) }
            worstObserver.optimize()
        }
    }
}