package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.hierarchy.GraphHierarchy
import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.rules.Rule

/**
 * Return the rules for a given area
 */
interface RuleProvider<AREA, H> {
    fun getRules(target: AREA): Collection<Rule<H>>
    fun getAllRules(): Map<AREA, Collection<Rule<H>>>

    fun withHierarchy(hierarchy: HierarchicElement<AREA>): HierarchicRuleProvider<AREA, H> = HierarchicRuleProviderImpl(this, hierarchy)
}

