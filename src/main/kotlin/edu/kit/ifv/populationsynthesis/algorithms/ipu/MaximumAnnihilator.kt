package edu.kit.ifv.populationsynthesis.algorithms.ipu

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import kotlin.math.abs

class MaximumAnnihilator : GenericIPU {
    override fun run(
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