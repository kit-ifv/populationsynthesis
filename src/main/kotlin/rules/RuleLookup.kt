package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.rules.contribution.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider

/**
 * Provide fast access to check whether an area has a rule for a given logic.
 */
class RuleLookup<AREA, T> private constructor(private val map: Map<AREA, Map<LogicIdentifier, Rule<T>>>) {

    val logics: Set<LogicIdentifier> by lazy {
        map.values.flatMap { it.keys }.toSet()
    }

    fun filter(predicate: (AREA) -> Boolean): RuleLookup<AREA, T> {
        return RuleLookup(map.filterKeys { predicate(it) })
    }

    operator fun get(area: AREA, identifier: LogicIdentifier): Rule<T>? {
        return map[area]?.get(identifier)
    }

    fun hasRule(area: AREA, identifier: LogicIdentifier): Boolean {
        return this[area, identifier] != null
    }

    companion object {
        fun <AREA, T> fromProvider(ruleProvider: RuleProvider<AREA, T>): RuleLookup<AREA, T> {
            return RuleLookup(ruleProvider.getAllRules().mapValues { it.value.associateBy { it.logic.identifier } })
        }
    }
}