package edu.kit.ifv.populationsynthesis.rules.covered

import edu.kit.ifv.populationsynthesis.rules.RuleSet

/**
 * If you do not know your rules, but your total target then you can also define a coverage group
 */
class ExplicitTargetCoverageGroup<T>(val rules: RuleSet<T>, target: Number) : CoverageGroup<T>, RuleSet<T> by rules {
    override val totalTarget: Double = target.toDouble()
}