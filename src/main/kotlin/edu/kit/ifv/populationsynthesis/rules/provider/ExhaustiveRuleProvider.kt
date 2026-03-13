package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.toRuleSet

/**
 * When the underlying rules are exhaustive, then we can always return a group of rules that combine to a
 * coverage group (they partition the observed attribute space completely)
 */
abstract class ExhaustiveRuleProvider<AREA, T> : RuleProvider<AREA, T> {
    final override fun getRules(target: AREA): RuleSet<T> = getRuleGroups(target).flatten().toRuleSet()
    abstract fun getRuleGroups(target: AREA): Collection<CoverageGroup<T>>
}

class MutableExhaustiveRuleProvider<AREA, T> : ExhaustiveRuleProvider<AREA, T>() {
    private val ruleMap: MutableMap<AREA, MutableList<CoverageGroup<T>>> = mutableMapOf()
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