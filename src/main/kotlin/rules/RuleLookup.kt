package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.rules.contribution.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider

/**
 * Provide fast access to check whether an area has a rule for a given logic. RuleProvider usually returns the
 * entire bulk of rules, which is cumbersome to operate upon. Does not contain composition strategy or anything
 * like that.
 */
class RuleLookup<AREA, T> private constructor(private val map: Map<AREA, Map<LogicIdentifier, Rule<T>>>) {

    val logics: Set<LogicIdentifier> by lazy {
        map.values.flatMap { it.keys }.toSet()
    }

    /**
     * Since the logics are fixed, we can assign an index for each logic.
     */
    val logicIndex: Map<LogicIdentifier, Int> by lazy {
        logics.withIndex().associate { it.value to it.index }
    }
    val areas = map.keys

    fun filter(predicate: (AREA) -> Boolean): RuleLookup<AREA, T> {
        return RuleLookup(map.filterKeys { predicate(it) })
    }

    operator fun get(area: AREA, identifier: LogicIdentifier): Rule<T>? {
        return map[area]?.get(identifier)
    }

    operator fun get(area: AREA): Set<IndexedRule<T>> {
        val rules = map[area]?.entries ?: return emptySet()
        return rules.map { (logic, rule) ->
            rule.withIndex(logicIndex[logic]!!)
        }.toSet()
    }

    fun getLogics(elements: Collection<AREA>): Set<IndexedRule<T>> {
        return elements.flatMap { get(it) }.toSet()
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

/**
 * Once the order of rules exist, then we can reference the index in the logic block.
 */
data class IndexedRule<T>(
    val index: Int,
    val rule: Rule<T>
) {
    fun toObserver(scalableVectors: Collection<ScalableVector>): RuleObserver {
        return RuleObserver.fromRule(rule, index, scalableVectors)
    }
}

fun <T> Collection<IndexedRule<T>>.toScalableVector(element: T): ScalableVector {
    return map { it.rule }.toScalableVector(element)
}

fun <T> Rule<T>.withIndex(index: Int): IndexedRule<T> {
    return IndexedRule(index, this)
}