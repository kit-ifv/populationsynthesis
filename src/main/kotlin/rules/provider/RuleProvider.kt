package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.rules.RuleSet

/**
 * Return the rules for a given area
 */
interface RuleProvider<AREA, T> {
    fun getRules(target: AREA): RuleSet<T>
    operator fun get(target: AREA) = getRules(target)
    fun getAllRules(): Map<AREA, RuleSet<T>>

    /**
     * Since a hierarchy can have more nodes than defined in the rule provider we must create a fresh instance
     * with updated elements
     */
    fun withHierarchy(hierarchy: HierarchicElement<AREA>): HierarchicRuleProvider<AREA, T> {
        val ruleProvider = MapRuleProvider<AREA, T>()
        getAllRules().forEach { (area, rules) ->
            ruleProvider.addRules(area, rules)
        }

        val missingNodes = hierarchy.getAllVertices() - getAllRules().keys
        missingNodes.forEach {
            ruleProvider.addRules(it, emptyList())
        }
        return HierarchicRuleProviderImpl(ruleProvider, hierarchy)

    }
}

