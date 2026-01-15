package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.composer.HierarchyRuleComposer
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup

class HierarchicalExhaustiveRuleProvider<AREA, H>(
    val ruleProvider: ExhaustiveRuleProvider<AREA, H>,
    override val composer: HierarchyRuleComposer<AREA, H>,
) : ExhaustiveRuleProvider<AREA, H>(), HierarchicRuleProvider<AREA, H> {

    override fun getAllRules(): Map<AREA, Collection<Rule<H>>> {
        return ruleProvider.getAllRules()
    }

    override fun getRuleGroups(target: AREA): Collection<CoverageGroup<H>> {
        return ruleProvider.getRuleGroups(target)
    }

}