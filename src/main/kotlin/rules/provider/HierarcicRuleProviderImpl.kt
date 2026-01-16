package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.composer.HierarchyComposer
import edu.kit.ifv.populationsynthesis.rules.composer.HierarchyRuleComposer


class HierarchicRuleProviderImpl<AREA, H>(
    private val ruleProvider: RuleProvider<AREA, H>,
    override val hierarchy: HierarchicElement<AREA>
): HierarchicRuleProvider<AREA, H> {
    override val composer: HierarchyRuleComposer<AREA, H> = HierarchyComposer(hierarchy)

    override fun getRules(target: AREA): Collection<Rule<H>> {
        return ruleProvider.getRules(target)
    }

    override fun getAllRules(): Map<AREA, Collection<Rule<H>>> {
        return ruleProvider.getAllRules()
    }
}