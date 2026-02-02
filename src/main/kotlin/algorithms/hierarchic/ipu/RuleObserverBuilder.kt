package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.rules.RuleLookup
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider
import edu.kit.ifv.populationsynthesis.rules.toScalableVector
import edu.kit.ifv.populationsynthesis.utils.EquivalenceClass
import edu.kit.ifv.populationsynthesis.utils.formEquivalenceClass

class RuleObserverBuilder<AREA, T>(
    hierarchicRuleProvider: RuleProvider<AREA, T>,
) {

    private val ruleLookup = RuleLookup.fromProvider(hierarchicRuleProvider)
    fun build(area: AREA, elements: Collection<T>): Collection<RuleObserver> {

        val indexedRules = ruleLookup.allContributions()
        val equivalenceClasses = elements.associateWith { indexedRules.toScalableVector(it) }.formEquivalenceClass()

        return build(area, equivalenceClasses)
    }

    fun build(area: AREA, equivalenceClass: EquivalenceClass<ScalableVector, T>): Collection<RuleObserver> {
        return ruleLookup[area].map {
            it.toObserver(equivalenceClass.representatives)
        }
    }

    fun buildFromVectors(area: AREA, vectors: Collection<ScalableVector>): Collection<RuleObserver> {
        return ruleLookup[area].map { it.toObserver(vectors) }
    }
}