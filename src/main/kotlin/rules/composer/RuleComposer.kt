package edu.kit.ifv.populationsynthesis.rules.composer

import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider

/**
 * A rule composer takes a target area, a [RuleProvider], and produces a novel [RuleSet] where additional, synthetic
 * rules could be derived.
 *
 * The primary use case is a [HierarchyRuleComposer] where rules are derived from both the element area as well as all
 * associated sub-areas.
 */
fun interface RuleComposer<AREA, T> {
    fun compose(target: AREA, ruleProvider: RuleProvider<AREA, T>): RuleSet<T>
}