package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.Signature
import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.measurement.Measurement
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider

/**
 * Provide fast access to check whether an area has a rule for a given logic. RuleProvider usually returns the
 * entire bulk of rules, which is cumbersome to operate upon. Does not contain composition strategy or anything
 * like that.
 */
class LogicIndexer<AREA, T> internal constructor(
    logicTranslation: Map<LogicIdentifier, Measurement<T>>,
    private val logicMap: Map<AREA, Set<IndexedLogic<T>>>,

    ) {


    val logics = logicTranslation.keys
    val size get() = logics.size
    val areas = logicMap.keys
    private val measurementFunctions: Set<Measurement<T>> = logicTranslation.values.toSet()
    private val logicIndices = logics.withIndex().associate { it.value to it.index }

    fun allMeasurements() = measurementFunctions

    fun createSignature(element: T): Signature? {
        val indexedMeasures: Map<Int, Double> = measurementFunctions.withIndex().associate { (index, logic) ->
            index to logic.measure(element)
        }
        if (indexedMeasures.isEmpty() || indexedMeasures.values.all { it == 0.0 })
            return null
        return Signature.fromMap(indexedMeasures)
    }

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

    fun <T> toIndexedRule(rule: Rule<T>): IndexedRule<T> {
        return IndexedRule(rule = rule, index = getIndex(rule))
    }

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


}

