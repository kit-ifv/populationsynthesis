package edu.kit.ifv.populationsynthesis.synthesis

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement

class UseLowestCoveredLeaf<AREA> : HandleRuleConflicts<AREA> {
    override fun removeConflicts(
        conflictingAreas: Collection<AREA>,
        hierarchicElement: HierarchicElement<AREA>,
    ): Collection<AREA> {
        val leafs = conflictingAreas.filter { hierarchicElement.isLeaf(it) }
        val completelyCovered = mutableSetOf<AREA>()
        completelyCovered.addAll(leafs)
        var nextRound = leafs.mapNotNull { hierarchicElement.getParent(it) }
        while (nextRound.isNotEmpty()) {
            nextRound.forEach { node ->
                val isCovered = hierarchicElement.getImmediateChildren(node).all { it in completelyCovered }
                if (isCovered) {
                    completelyCovered.add(node)
                }
            }
            nextRound = nextRound.mapNotNull { hierarchicElement.getParent(it) }
        }

        val (removable, unremovable) = conflictingAreas.filter { it !in leafs }.partition { it in completelyCovered }
        val mustRemove = unremovable.flatMap { hierarchicElement.getAllChildren(it) }.toSet()

        return conflictingAreas.filter { it !in removable && it !in mustRemove }
    }
}