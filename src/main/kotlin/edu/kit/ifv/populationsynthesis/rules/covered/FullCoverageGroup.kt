package edu.kit.ifv.populationsynthesis.rules.covered

import edu.kit.ifv.populationsynthesis.rules.RuleSet

/**
 * If you know that your rules cover all possible cases then the total target can be derived from the rules themselves
 */
class FullCoverageGroup<T>(val rules: RuleSet<T>) : CoverageGroup<T>, RuleSet<T> by rules {
    override val totalTarget: Double = sumOf { it.target }
}

