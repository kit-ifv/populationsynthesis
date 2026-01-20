package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleGenerator
import edu.kit.ifv.populationsynthesis.rules.RuleSet

class MapRuleProvider<AREA, T>(
    private val ruleMap: MutableMap<AREA, RuleSet<T>> = mutableMapOf(),
) :
    RuleProvider<AREA, T> {

    override fun getRules(target: AREA): RuleSet<T> {
        return ruleMap[target] ?: emptySet<>()
    }

    override fun getAllRules(): Map<AREA, RuleSet<T>> {
        return ruleMap
    }

    fun addRules(area: AREA, rules: List<Rule<T>>) {
        ruleMap[area] = ruleMap.getOrDefault(area, emptyList()) + rules
    }
    operator fun set(target: AREA, rules: List<Rule<T>>) {
        ruleMap[target] = rules
    }
    fun addRules(area: AREA, ruleGenerator: RuleGenerator<T>) {
        addRules(area, ruleGenerator.generateRules())

    }

    fun addRule(area: AREA, rule: Rule<T>) {
        ruleMap[area] = ruleMap.getOrDefault(area, emptyList()) + rule
    }
    companion object {
        fun <AREA, H> fromMap(
            map: Map<AREA, List<Rule<H>>>
        ): MapRuleProvider<AREA, H> =
            MapRuleProvider(map.toMutableMap())
    }
}