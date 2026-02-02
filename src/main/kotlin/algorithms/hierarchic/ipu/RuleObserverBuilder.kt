package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.rules.LogicIndexer
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider
import edu.kit.ifv.populationsynthesis.rules.toScalableVector
import edu.kit.ifv.populationsynthesis.utils.EquivalenceClass
import edu.kit.ifv.populationsynthesis.utils.formEquivalenceClass

data class RuleObserverBuilder<AREA, T>(
    val ruleProvider: RuleProvider<AREA, T>,
    val logicIndexer: LogicIndexer<AREA, T>
) {
    constructor(ruleProvider: RuleProvider<AREA, T>) : this(ruleProvider, LogicIndexer.fromProvider(ruleProvider))

    fun build(area: AREA, elements: Collection<T>): Collection<RuleObserver> {

        val indexedRules = logicIndexer.allContributions()
        val equivalenceClasses = elements.associateWith { indexedRules.toScalableVector(it) }.formEquivalenceClass()

        return build(area, equivalenceClasses)
    }

    fun build(area: AREA, equivalenceClass: EquivalenceClass<ScalableVector, T>): Collection<RuleObserver> {
        return with(logicIndexer) {
            ruleProvider.buildObservers(area, equivalenceClass.representatives)
        }

    }

    fun buildFromVectors(area: AREA, vectors: Collection<ScalableVector>): Collection<RuleObserver> {
        return ruleProvider.getRules(area).map { RuleObserver.fromRule(it, logicIndexer.getIndex(it), vectors) }
    }

    fun getLogics(elements: Collection<AREA>) = logicIndexer.getLogics(elements)
}