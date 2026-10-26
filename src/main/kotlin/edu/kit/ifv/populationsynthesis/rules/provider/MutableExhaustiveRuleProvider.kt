package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.toRuleSet

class MutableExhaustiveRuleProvider<AREA, T> : ExhaustiveRuleProvider<AREA, T>() {
    private val ruleMap: MutableMap<AREA, MutableList<CoverageGroup<T>>> = mutableMapOf()
    override val keys get() = ruleMap.keys
    override fun getRuleGroups(target: AREA): Collection<CoverageGroup<T>> {
        return ruleMap[target] ?: emptyList()
    }

    override fun get(
        target: AREA,
        logicIdentifier: LogicIdentifier
    ): Rule<T>? {
        return getRules(target)[logicIdentifier]
    }

    fun add(target: AREA, rules: CoverageGroup<T>) {
        val coverageList = ruleMap.getOrPut(target) { mutableListOf() }
        coverageList.add(rules)
    }

    override fun getAllRules(): Map<AREA, RuleSet<T>> {
        return ruleMap.mapValues { it.value.flatten().toRuleSet() }
    }
}