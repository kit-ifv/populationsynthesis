package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.rules.NamedContribution
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.fuse
import edu.kit.ifv.populationsynthesis.synthesis.HandleRuleConflicts
import edu.kit.ifv.populationsynthesis.synthesis.UseLowestCoveredLeaf
import edu.kit.ifv.populationsynthesis.utils.partitionValues

@Suppress("ComplexInterface", "TooManyFunctions")
interface HierarchicalRuleProviderLegacy<AREA, H> : RuleProvider<AREA, H> {
    val hierarchy: HierarchicElement<AREA>

    fun partition(predicate: (AREA) -> Boolean): Pair<
        HierarchicalRuleProviderLegacy<AREA, H>,
        HierarchicalRuleProviderLegacy<AREA, H>
        >

    fun getAllDescendants(target: AREA) = hierarchy.getAllDescendants(target)
    fun getAllDescendantRules(target: AREA) = getAllDescendants(target).associateWith { getRules(it) }

    @Deprecated("Use conflict free rules instead.")
    fun getAllRulesFor(target: AREA): Map<AREA, Collection<Rule<H>>> {
        val rules = getAllDescendantRules(target)
        return rules + (target to getRules(target))
    }

    fun getAllRuleLogics(): List<NamedContribution<H>>
    operator fun contains(area: AREA): Boolean

    /**
     * Get all rules registered in this object and return all areas that have at least 1 rule attached
     */

    fun getAllLeafs() = hierarchy.getAllLeafs()

    /**
     * Prepare the rules so that no hierarchical dependency defines the same rule twice. It is ok if all child nodes
     * have a shared rule logic, or an ancestor for the entire group, but never should a node and any of its ancestors
     * share a rule logic.
     *
     * However the rules returned should contain all rules that occur in either this area or any subareas so that no
     * rule is lost.
     */
    fun getConflictFreeRules(
        target: AREA,
        conflictResolution: HandleRuleConflicts<AREA> = UseLowestCoveredLeaf(),
    ): List<Rule<H>> {
        val rules = getAllRulesFor(target)

        val logicSeparated = rules.entries.flatMap { (k, v) ->
            v.map { it.logic to k }
        }
        val mappedRules = rules.mapValues { (_, v) -> v.associateBy { it.logic } }
        val groupedLogics = logicSeparated.groupBy({ it.first }, { it.second })
        val (conflicts, safe) = groupedLogics.partitionValues { hierarchy.getDependencies(it).isNotEmpty() }

        val updates = conflicts.entries.associate { it.key to conflictResolution.removeConflicts(it.value, hierarchy) }

        val fusedRules = (updates + safe).map { (k, v) ->
            val allActiveRules = v.map { mappedRules[it]!![k]!! }
            allActiveRules.fuse() //"Fused Rules for ${k.logic} summing ${allActiveRules.size} elements"
        }
        return fusedRules
    }

    /**
     * AN area without sub areas can be considered final
     */
    fun isFinal(target: AREA) = getSubAreas(target).isEmpty()

    fun getSubAreas(target: AREA) = hierarchy.getChildren(target)

//    fun results(area: AREA, output: List<H>): List<AreaIPUOutput<AREA>> {
//        return getRules(area).map {
//            val data = IPUOutputLog(it.description, it.target, it.evaluate(output))
//            AreaIPUOutput(area, data)
//        }
//    }
//    fun results(output: Map<AREA, List<H>>) = output.entries.flatMap { results(it.key, it.value) }
//    fun verify(
//        output: Collection<Pair<AREA, Collection<H>>>,
//        metric: Metric = Metric.standardizedRootMeanSquaredResidual
//    ) = verify(
//        output.toMap(),
//        metric
//    )

//    fun evaluate(output: Map<AREA, Collection<H>>): List<AreaIPUOutput<AREA>> {
//        val ruleMapping = getAllRules()
//        val ruleResults = ruleMapping.flatMap { (area, rules) ->
//            val subareas = listOf(area) + (runCatching { getAllDescendants(area)}.getOrNull() ?:emptyList())
//            if(subareas.any {it in output}) {
//                val currentHHs = subareas.flatMap {
//                    output[it] ?: emptyList()
//                }
//                rules.toIPUOutput(area, currentHHs)
//            } else { emptyList() }
//        }
//        return ruleResults
//    }
//
//    fun verify(output: Map<AREA, Collection<H>>, metric: Metric = Metric.standardizedRootMeanSquaredResidual): Double {
//        val output = evaluate(output)
//        return metric.evaluate(output.map { it.original.expected to it.original.actual })
//    }
}