package edu.kit.ifv.populationsynthesis.synthesis

import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider

/**
 * Partition the seed households based on some constraint
 */
class PartitionedSynthesis<AREA, T, K>(
    override val ruleProvider: HierarchicRuleProvider<AREA, in T>,
    private val seedElements: Collection<T>,
    private val keySelector: (AREA) -> K,
    private val elementFilter: (T, K) -> Boolean,
    private val creationInstruction: PartitionedSynthesis<AREA, T, K>.(Collection<T>) -> HierarchicSynthesis<AREA, T>
)  : RuleBasedPopulationSynthesis<AREA, T> {


    override fun synthesizeAll(): Map<AREA, List<T>> {
        return synthesize(ruleProvider.keys)
    }


    override fun synthesize(targetAreas: Collection<AREA>): Map<AREA, List<T>> {
        val partitionedAreas = targetAreas.groupBy(keySelector)
        val output = partitionedAreas.flatMap { (key, areas) ->
            val validElements = seedElements.filter { elementFilter(it, key) }
            val partitionSynthesis = creationInstruction(validElements)
            partitionSynthesis.synthesize(areas).entries
        }.associate { it.key to it.value }
        return output
    }
}