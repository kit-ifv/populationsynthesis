package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.toRuleSet

/**
 * When the underlying rules are exhaustive, then we can always return a group of rules that combine to a
 * coverage group (they partition the observed attribute space completely)
 */
abstract class ExhaustiveRuleProvider<AREA, T> : RuleProvider<AREA, T> {
    final override fun getRules(target: AREA): RuleSet<T> = getRuleGroups(target).flatten().toRuleSet()
    abstract fun getRuleGroups(target: AREA): Collection<CoverageGroup<T>>
}