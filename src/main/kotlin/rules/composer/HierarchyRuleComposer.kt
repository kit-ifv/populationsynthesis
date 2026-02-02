package edu.kit.ifv.populationsynthesis.rules.composer

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement

/**
 * A hierarchy based rule composition uses a [HierarchicElement] to derive new synthetic rules. The core ordering
 * of the areas can be accessed via the [hierarchy].
 *
 * As Example consider the Hierarchy A1->B, A2->B. When we have a rule defined in A1 (Rule:= Age [10,20], 10.0) &
 * A2 (Rule:= Age [10,20], 20.0) then we can automatically induce a Rule B (Rule:= Age[10,20], 30).
 *
 */
interface HierarchyRuleComposer<AREA, T> : RuleComposer<AREA, T> {
    val hierarchy: HierarchicElement<AREA>
}