package edu.kit.ifv.populationsynthesis.synthesis

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement

fun interface HandleRuleConflicts<AREA> {
    fun removeConflicts(
        conflictingAreas: Collection<AREA>,
        hierarchicElement: HierarchicElement<AREA>,
    ): Collection<AREA>
}