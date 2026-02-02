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
class LogicIndexer<AREA, T> private constructor(
    val logics: Set<LogicIdentifier>,
    private val logicMap: Map<AREA, Set<IndexedLogic<T>>>,
) {

    companion object {
        fun <AREA, T> fromProvider(ruleProvider: RuleProvider<AREA, T>): LogicIndexer<AREA, T> {
            val map = ruleProvider.getAllRules()
            val logics = map.values.flatMap { it.map { it.identifier } }.toSet()
            val indexer = logics.withIndex().associate { it.value to it.index }
            val newlyMap: Map<AREA, Set<IndexedLogic<T>>> = map.mapValues {
                it.value.map { rule ->
                    IndexedLogic(
                        indexer[rule.identifier]!!,
                        rule.identifier,
                        rule.logic
                    )
                }.toSet()
            }
            return LogicIndexer(
                logics,
                newlyMap,
            )
        }
    }

    private val logicIndices = logics.withIndex().associate { it.value to it.index }
    private val contributionFunctions = logicMap.values.flatMap { it.map { it.logic } }
    fun allContributions() = contributionFunctions


    val areas = logicMap.keys
    fun getIndex(rule: Rule<T>): Int {
        return logicIndices[rule.identifier]
            ?: throw IllegalStateException("Rule ${rule.identifier} not found in this logic indexer logics=$logics")
    }

    fun filter(predicate: (AREA) -> Boolean): LogicIndexer<AREA, T> {
        val newMap = logicMap.filterKeys { predicate(it) }
        val keptLogics = newMap.flatMap { it.value.map { it.logicIdentifier } }.toSet()


        return LogicIndexer(keptLogics, logicMap.filterKeys { predicate(it) })
    }

    /**
     * Return the indexed logics that are present in the requested elements.
     */
    fun getLogics(elements: Collection<AREA> = logicMap.keys): Set<IndexedLogic<T>> {
        return elements.flatMap { getLogic(it) }.toSet()
    }

    fun getLogic(element: AREA): Set<IndexedLogic<T>> {
        return logicMap[element] ?: emptySet()
    }

    fun hasRule(area: AREA, identifier: LogicIdentifier): Boolean {
        return identifier in (logicMap[area]?.map { it.logicIdentifier } ?: return false)
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

data class IndexedLogic<T>(
    val index: Int,
    val logicIdentifier: LogicIdentifier,
    val logic: Contribution<T>
) : Contribution<T> by logic {

    fun <AREA> toIndexedRule(target: AREA, ruleProvider: RuleProvider<AREA, T>): IndexedRule<T>? {
        val rule = ruleProvider[target, logicIdentifier] ?: return null
        return IndexedRule(index = index, rule = rule)
    }
}

