package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.measurement.Measurement
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider

/**
 * Provide fast access to check whether an area has a rule for a given logic. RuleProvider usually returns the
 * entire bulk of rules, which is cumbersome to operate upon. Does not contain composition strategy or anything
 * like that.
 */
class LogicIndexer<AREA, T> internal constructor(
    logicMape: Map<LogicIdentifier, Measurement<T>>,
    private val logicMap: Map<AREA, Set<IndexedLogic<T>>>,

) {

    companion object {
        fun <AREA, T> fromProvider(ruleProvider: RuleProvider<AREA, T>): LogicIndexer<AREA, T> {
            val map = ruleProvider.getAllRules()
            val logics = map.values.flatMap { it.map { it.identifier to it.logic } }.toMap()
            val indexer = logics.keys.withIndex().associate { it.value to it.index }
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
    val logics = logicMape.keys
    val size get() = logics.size
    private val measurementFunctions: Set<Measurement<T>> = logicMape.values.toSet()
    private val logicIndices = logics.withIndex().associate { it.value to it.index }

    fun allMeasurements() = measurementFunctions


    val areas = logicMap.keys
    fun getIndex(rule: Rule<*>): Int {
        return logicIndices[rule.identifier]
            ?: throw IllegalStateException("Rule ${rule.identifier} not found in this logic indexer logics=$logics")
    }

    fun filter(predicate: (AREA) -> Boolean): LogicIndexer<AREA, T> {
        val newMap = logicMap.filterKeys { predicate(it) }
        val keptLogics = newMap.flatMap { it.value.map { it.logicIdentifier to it.logic } }.toMap()


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

