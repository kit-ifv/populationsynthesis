package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.rules.LogicIndexer
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.sumRule

/**
 * Return the rules for a given area
 */
interface RuleProvider<AREA, T> {
    fun getRules(target: AREA): RuleSet<T>
    operator fun get(target: AREA) = getRules(target)
    fun getAllRules(): Map<AREA, RuleSet<T>>

    operator fun get(target: AREA, logicIdentifier: LogicIdentifier): Rule<T>?

    /**
     * Link this rule provider with a [HierarchicElement] to generate a [HierarchicRuleProvider].
     * Will construct a fresh rule provider from scratch and copy rules from this into the new provider
     * Nodes from the hierarchy that are not present in this rule provider will be added to the new rule provider
     * and have an empty rule set defined for them.
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

    fun getSum(targets: Collection<AREA>, logicIdentifier: LogicIdentifier): Rule<T> {
        val rules = targets.mapNotNull { get(it, logicIdentifier) }
        return rules.sumRule()
    }

    context(logicIndexer: LogicIndexer<AREA, T>)
    fun buildObservers(target: AREA, vectors: Collection<ScalableVector>): Collection<RuleObserver> {
        val li = logicIndexer.getLogic(target)
        return li.mapNotNull {
            it.toIndexedRule(target, this)
        }.map { it.toObserver(vectors) }

    }
}

