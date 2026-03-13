package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.rules.MutableRuleSet
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleGenerator
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier

class MapRuleProvider<AREA, T>(
    private val ruleMap: MutableMap<AREA, MutableRuleSet<T>> = mutableMapOf(),
    private val construction: () -> MutableRuleSet<T> = ::MutableRuleSet
) :
    RuleProvider<AREA, T> {

    override fun getRules(target: AREA): RuleSet<T> {
        return ruleMap[target] ?: MutableRuleSet()
    }

    override fun getAllRules(): Map<AREA, RuleSet<T>> {
        return ruleMap
    }

    override fun get(target: AREA, logicIdentifier: LogicIdentifier): Rule<T>? {
        return ruleMap[target]?.get(logicIdentifier)
    }

    fun addRules(area: AREA, rules: Collection<Rule<T>>) {
        val orDefault = ruleMap.getOrPut(area) {
            construction()
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

    fun addRules(area: AREA, ruleGenerator: RuleGenerator<in T>) {
        addRules(area, ruleGenerator.generateRules())

    }

    fun loadFromOtherRuleProvider(
        ruleProvider: RuleProvider<AREA, in T>,
        predicate: (Map.Entry<AREA, RuleSet<T>>) -> Boolean = { true },
    ) {
        val rules = ruleProvider.getAllRules().filter(predicate)
        rules.forEach { area, rules ->
            addRules(area, rules)
        }
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