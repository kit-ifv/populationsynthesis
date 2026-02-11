package edu.kit.ifv.populationsynthesis.rules.covered

import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.toRuleSet

/**
 * If you know that your rules cover all possible cases then the total target can be derived from the rules themselves
 */
class FullCoverageGroup<T>(val rules: RuleSet<T>) : CoverageGroup<T>, RuleSet<T> by rules {
    override val totalTarget: Double = sumOf { it.target }
}

/**
 * If you do not know your rules, but your total target then you can also define a coverage group
 */
class ExplicitTargetCoverageGroup<T>(val rules: RuleSet<T>, target: Number) : CoverageGroup<T>, RuleSet<T> by rules {
    override val totalTarget: Double = target.toDouble()
}