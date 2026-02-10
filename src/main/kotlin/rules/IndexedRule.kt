package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector

/**
 * Once the order of rules exist, then we can reference the index in the logic block.
 */
data class IndexedRule<T>(
    val index: Int,
    val rule: Rule<T>
) {
    fun toObserver(scalableVectors: Collection<ScalableVector>): RuleObserver {
        return RuleObserver.fromRule(rule, index, scalableVectors)
    }
}