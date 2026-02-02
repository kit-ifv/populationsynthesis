package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.composer.HierarchyComposer
import edu.kit.ifv.populationsynthesis.rules.composer.HierarchyRuleComposer
import edu.kit.ifv.populationsynthesis.rules.contribution.LogicIdentifier


class HierarchicRuleProviderImpl<AREA, T>(
    private val ruleProvider: RuleProvider<AREA, T>,
    override val hierarchy: HierarchicElement<AREA>
) : HierarchicRuleProvider<AREA, T> {
    override val composer: HierarchyRuleComposer<AREA, T> = HierarchyComposer(hierarchy)

    override fun getRules(target: AREA): RuleSet<T> {
        return ruleProvider.getRules(target)
    }

    override fun getAllRules(): Map<AREA, RuleSet<T>> {
        return ruleProvider.getAllRules()
    }

    override fun get(
        target: AREA,
        logicIdentifier: LogicIdentifier
    ): Rule<T>? {
        return ruleProvider[target, logicIdentifier]
    }
}