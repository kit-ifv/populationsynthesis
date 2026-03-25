package edu.kit.ifv.populationsynthesis.synthesis

import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

abstract class HierarchicSynthesis<AREA, T>(
    override val ruleProvider: HierarchicRuleProvider<AREA, in T>
) : RuleBasedPopulationSynthesis<AREA, T> {
    val hierarchy = ruleProvider.hierarchy
    final override fun synthesize(targetAreas: List<AREA>): Map<AREA, List<T>> {
        // Trace roots runs up to the highest ancestor. Should take into account intermediate areas. Ignore empty trees with no rules, they get nothing
        val rootRegions = hierarchy.groupByHighestAncestor(targetAreas)
            .filter { it.value.any { ruleProvider.getRules(it).isNotEmpty() } }
//        val independentRegions = separateIrrelevantRegions(rootRegions) Unnecessary, should be handled by hierarchy beforehand

//        TODO("Insert a method to inject pipeline to extract stuff.")
        val out = runBlocking {
            rootRegions.entries.map { (root, childs) ->
                async(Dispatchers.Default) {
                    val result = synthesize(root, childs)
                    result
                }
            }.awaitAll()
                .flatMap { it.entries }
                .associate { it.key to it.value }
        }
        return out.filterKeys { it in targetAreas }
    }

    final override fun synthesize(target: AREA): Map<AREA, List<T>> {
        return synthesize(hierarchy.getAllChildren(target).toList())
    }
    override fun synthesizeAll(): Map<AREA, List<T>> {
        return synthesize(hierarchy.getAllLeafs())
    }

    /**
     * Guess we need this function to implement the logic afterwards.
     */
    protected abstract fun synthesize(
        highestArea: AREA,
        targetAreas: Collection<AREA>,
    ): Map<AREA, List<T>>


}