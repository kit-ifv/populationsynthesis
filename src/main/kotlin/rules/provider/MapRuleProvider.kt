package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleGenerator

class MapRuleProvider<AREA, H>(
    private val ruleMap: MutableMap<AREA, List<Rule<H>>> = mutableMapOf(),
) :
    RuleProvider<AREA, H> {

    override fun getRules(target: AREA): Collection<Rule<H>> {
        return ruleMap[target] ?: emptyList()
    }

    override fun getAllRules(): Map<AREA, Collection<Rule<H>>> {
        return ruleMap
    }

    fun addRules(area: AREA, rules: List<Rule<H>>) {
        ruleMap[area] = ruleMap.getOrDefault(area, emptyList()) + rules
    }
    operator fun set(target: AREA, rules: List<Rule<H>>) {
        ruleMap[target] = rules
    }
    fun addRules(area: AREA, ruleGenerator: RuleGenerator<H>) {
        addRules(area, ruleGenerator.generateRules())

    }
    companion object {
        fun <AREA, H> fromMap(
            map: Map<AREA, List<Rule<H>>>
        ): MapRuleProvider<AREA, H> =
            MapRuleProvider(map.toMutableMap())
    }
}