package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.ipu

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.MatrixSolver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector

class TabooListIPU(
    private val blockAmount: Int,
    private val iterations: Int = 1000,
) : MatrixSolver {
    override fun solve(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ) {
        val tabooList = mutableListOf<RuleObserver>()
        repeat(iterations) {
            val targetObserver = observers.sortedByDescending { it.absoluteDifference }.dropWhile {
                it in tabooList
            }.first()
            targetObserver.optimize()
            if (tabooList.size > blockAmount) {
                tabooList.removeFirst()
            }
            tabooList.add(targetObserver)

        }


    }
}