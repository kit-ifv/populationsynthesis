package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.rules.contribution.Contribution
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

    val logicFunctions: Map<LogicIdentifier, Contribution<T>> by lazy {

        map.values.flatMap { it.entries }.associate { (id, c) ->
            id to c.logic
        }

    }

    fun allContributions() = logicFunctions.values

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

    operator fun get(area: AREA, identifier: LogicIdentifier): IndexedRule<T>? {
        val rule = map[area]?.get(identifier) ?: return null
        return rule.withIndex()
    }

    operator fun get(area: AREA): Set<IndexedRule<T>> {
        val rules = map[area]?.entries ?: return emptySet()
        return rules.map { (_, rule) ->
            rule.withIndex()
        }.toSet()
    }

    private fun Rule<T>.withIndex(): IndexedRule<T> {
        return IndexedRule(rule = this, index = getLogicIndex(this))
    }

    private fun getLogicIndex(rule: Rule<T>): Int {
        return logicIndex[rule.logic.identifier]
            ?: throw IllegalStateException("A rule lookup should always be able to produce an index for a rule. Failed for rule=$rule \n logicMapping $logicIndex")
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

fun <T> Collection<IndexedRule<T>>.reindex(): Collection<IndexedRule<T>> {
    TODO()
}

fun <T> Collection<IndexedRule<T>>.toScalableVector(element: T): ScalableVector {
    return map { it.rule }.toScalableVectorOld(element)
}

fun <T> Rule<T>.withIndex(index: Int): IndexedRule<T> {
    return IndexedRule(index, this)
}