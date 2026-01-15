package edu.kit.ifv.populationsynthesis.rules.composer

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement

/**
 * A hierarchy rule composer uses a hierarchy structure to derive rules.
 */
interface HierarchyRuleComposer<AREA, H>: RuleComposer<AREA, H> {
    val hierarchy: HierarchicElement<AREA>
}