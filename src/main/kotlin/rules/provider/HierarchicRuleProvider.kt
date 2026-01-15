package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.rules.composer.HierarchyRuleComposer

/**
 * A hierarchic rule provider uses a hierarchy to compose indirect rules.
 */
interface HierarchicRuleProvider<AREA, H>: ComposingRuleProvider<AREA, H> {
    override val composer: HierarchyRuleComposer<AREA, H>
    val hierarchy: HierarchicElement<AREA> get() = composer.hierarchy
}