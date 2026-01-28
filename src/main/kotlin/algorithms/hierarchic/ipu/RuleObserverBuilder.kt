package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.rules.RuleLookup
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.rules.toScalableVector
import edu.kit.ifv.populationsynthesis.utils.EquivalenceClass
import edu.kit.ifv.populationsynthesis.utils.formEquivalenceClass

class RuleObserverBuilder<AREA, T>(
    hierarchicRuleProvider: HierarchicRuleProvider<AREA, T>,
) {
    private val hierarchy = hierarchicRuleProvider.hierarchy
    private val ruleLookup = RuleLookup.Companion.fromProvider(hierarchicRuleProvider)
    fun build(area: AREA, elements: Collection<T>): Collection<RuleObserver> {
        val parents = hierarchy.getAllAncestors(area)

        val indexedRules = ruleLookup.getLogics(parents + area)
        val equivalenceClasses = elements.associateWith { indexedRules.toScalableVector(it) }.formEquivalenceClass()

        return build(area, equivalenceClasses)
    }

    fun build(area: AREA, equivalenceClass: EquivalenceClass<ScalableVector, T>): Collection<RuleObserver> {
        return ruleLookup[area].map { it.toObserver(equivalenceClass.representatives) }
    }

    fun buildFromVectors(area: AREA, vectors: Collection<ScalableVector>): Collection<RuleObserver> {
        return ruleLookup[area].map { it.toObserver(vectors) }
    }
}