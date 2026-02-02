package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.rules.composer.HierarchyRuleComposer

/**
 * A hierarchic rule provider uses a hierarchy to compose indirect rules.
 */
interface HierarchicRuleProvider<AREA, T> : ComposingRuleProvider<AREA, T> {
    override val composer: HierarchyRuleComposer<AREA, T>
    val hierarchy: HierarchicElement<AREA> get() = composer.hierarchy
}