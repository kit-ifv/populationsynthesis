package edu.kit.ifv.populationsynthesis.rules.covered

import edu.kit.ifv.populationsynthesis.rules.Rule

/**
 * If you know that your rules cover all possible cases then the total target can be derived from the rules themselves
 */
class FullCoverageGroup<T>(override val rules: List<Rule<T>>): CoverageGroup<T>, List<Rule<T>> by rules {
    override val totalTarget: Double = rules.sumOf { it.target }
}

