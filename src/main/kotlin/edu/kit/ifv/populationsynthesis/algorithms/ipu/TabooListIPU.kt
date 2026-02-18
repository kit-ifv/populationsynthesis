package edu.kit.ifv.populationsynthesis.algorithms.ipu

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector

class TabooListIPU(
    private val blockAmount: Int,
    private val iterations: Int = 1000,
): GenericIPU {
    override fun run(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ) {
        val tabooList = mutableListOf<RuleObserver>()
        repeat(iterations) {
            val targetObserver = observers.sortedByDescending { it.absoluteDifference }.dropWhile {
                it in tabooList
            }.first()
            targetObserver.optimize()
            if(tabooList.size > blockAmount) {
                tabooList.removeFirst()
            }
            tabooList.add(targetObserver)

        }


    }
}