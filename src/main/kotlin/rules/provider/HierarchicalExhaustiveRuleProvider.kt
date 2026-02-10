package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.composer.HierarchyRuleComposer
import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup

class HierarchicalExhaustiveRuleProvider<AREA, T>(
    val ruleProvider: ExhaustiveRuleProvider<AREA, T>,
    override val composer: HierarchyRuleComposer<AREA, T>,
) : ExhaustiveRuleProvider<AREA, T>(), HierarchicRuleProvider<AREA, T> {

    override fun getAllRules(): Map<AREA, RuleSet<T>> {
        return ruleProvider.getAllRules()
    }

    override fun get(
        target: AREA,
        logicIdentifier: LogicIdentifier
    ): Rule<T>? {
        return ruleProvider[target, logicIdentifier]
    }

    override fun getRuleGroups(target: AREA): Collection<CoverageGroup<T>> {
        return ruleProvider.getRuleGroups(target)
    }

}