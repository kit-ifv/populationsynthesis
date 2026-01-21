package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.rules.MutableRuleSet
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleGenerator
import edu.kit.ifv.populationsynthesis.rules.RuleSet

class MapRuleProvider<AREA, T>(
    private val ruleMap: MutableMap<AREA, MutableRuleSet<T>> = mutableMapOf(),
) :
    RuleProvider<AREA, T> {

    override fun getRules(target: AREA): RuleSet<T> {
        return ruleMap[target] ?: MutableRuleSet()
    }

    override fun getAllRules(): Map<AREA, RuleSet<T>> {
        return ruleMap
    }

    fun addRules(area: AREA, rules: Collection<Rule<T>>) {
        val orDefault = ruleMap.getOrPut(area) {
            MutableRuleSet()
        }
        orDefault.add(rules)
    }

    operator fun set(target: AREA, rules: List<Rule<T>>) {
        val ruleset = MutableRuleSet<T>().apply {
            add(rules)
        }
        set(target, ruleset)
    }
    fun set(target: AREA, rules: MutableRuleSet<T>) {
        ruleMap[target] = rules
    }

    fun addRules(area: AREA, ruleGenerator: RuleGenerator<T>) {
        addRules(area, ruleGenerator.generateRules())

    }

    fun addRule(area: AREA, rule: Rule<T>) {
        val ruleset = ruleMap.getOrPut(area) {
            MutableRuleSet.empty()
        }
        ruleset.add(rule)
    }

    companion object {
        fun <AREA, T> fromMap(
            map: Map<AREA, List<Rule<T>>>
        ): MapRuleProvider<AREA, T> {
            val provider = MapRuleProvider<AREA, T>().apply {
                map.forEach { (key, value) ->
                    addRules(key, value)
                }
            }

            return provider
        }

    }
}