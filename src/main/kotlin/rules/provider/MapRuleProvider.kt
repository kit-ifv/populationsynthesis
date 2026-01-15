package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.rules.Rule

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

    companion object {
        fun <AREA, H> fromMap(
            map: Map<AREA, List<Rule<H>>>
        ): MapRuleProvider<AREA, H> =
            MapRuleProvider(map.toMutableMap())
    }
}