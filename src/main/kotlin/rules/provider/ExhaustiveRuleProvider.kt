package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.covered.FullCoverageGroup

/**
 * When the underlying rules are exhaustive, then we can always return a group of rules that combine to a
 * coverage group (they partition the observed attribute space completely)
 */
abstract class ExhaustiveRuleProvider<AREA, H> : RuleProvider<AREA, H> {
    final override fun getRules(target: AREA): Collection<Rule<H>> = getRuleGroups(target).flatten()
    abstract fun getRuleGroups(target: AREA): Collection<CoverageGroup<H>>
}