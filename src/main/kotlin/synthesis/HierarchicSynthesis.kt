package edu.kit.ifv.populationsynthesis.synthesis

import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

abstract class HierarchicSynthesis<AREA, H>(
    override val ruleProvider: HierarchicRuleProvider<AREA, H>
): RuleBasedPopulationSynthesis<AREA, H> {
    val hierarchy = ruleProvider.hierarchy
    final override fun synthesize(targetAreas: List<AREA>): Map<AREA, List<H>>   {
        // Trace roots runs up to the highest ancestor. Should take into account intermediate areas.
        val rootRegions = hierarchy.groupByHighestAncestor(targetAreas)
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

    override fun synthesizeAll(): Map<AREA, List<H>> {
        return synthesize(hierarchy.getAllLeafs())
    }

    /**
     * Guess we need this function to implement the logic afterwards.
     */
    protected abstract fun synthesize(
        highestArea: AREA,
        targetAreas: Collection<AREA>,
    ): Map<AREA, List<H>>
    private fun Map<AREA, *>.hasIrrelevantKeys(): Boolean {
        return keys.any { isIrrelevant(it) }
    }
    // We don't need to bother catering to areas that have no rules attached to them. TODO also if their ruleset is entirely dominated by the descendants.
    private fun isIrrelevant(area: AREA) = ruleProvider.getRules(area).isEmpty()

    private fun Map<AREA, *>.irrelevantKeys() = keys.filter { isIrrelevant(it) }
    private fun separateIrrelevantRegions(original: Map<AREA, Collection<AREA>>): Map<AREA, Collection<AREA>> {
        val workspace = original.toMutableMap()
        // TODO when none of the higher elements in the hierarchy define rules then the target area should
        //   point to itself, but right now it will point
        val flatTargets = original.values.flatten()
        while (workspace.hasIrrelevantKeys()) {
            val irrelevantKeys = workspace.irrelevantKeys()
            irrelevantKeys.forEach { key ->
                println("Killing $key because no rules")
                // Just to be safe that all values from the irrelevant key are touched by the children. Theoretically this
                // should never be able to occur, based on the graph structure and inputs
                val safetyCheck = workspace[key]!!.filter { it in flatTargets }
                workspace.remove(key)
                val children = hierarchy.getImmediateChildren(key)

                val newInserts = children.associateWith { child ->
                    val grandChildren = hierarchy.getAllChildren(child)
                    grandChildren
                }
                workspace.putAll(newInserts)

                require(newInserts.values.flatten().containsAll(safetyCheck)) {
                    "This should not happen ever"
                }
            }
        }

        return workspace.mapValues { it.value.filter { it in flatTargets } }
    }
}