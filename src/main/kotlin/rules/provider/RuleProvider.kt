package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleSet

/**
 * Return the rules for a given area
 */
interface RuleProvider<AREA, T> {
    fun getRules(target: AREA): RuleSet<T>

    operator fun get(target: AREA) = getRules(target)
    fun getAllRules(): Map<AREA, RuleSet<T>>

    fun withHierarchy(hierarchy: HierarchicElement<AREA>): HierarchicRuleProvider<AREA, T> = HierarchicRuleProviderImpl(this, hierarchy)
}

