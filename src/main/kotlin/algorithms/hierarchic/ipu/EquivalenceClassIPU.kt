package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.TargetNumberObserver
import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU
import edu.kit.ifv.populationsynthesis.rules.IndexedLogic
import edu.kit.ifv.populationsynthesis.rules.LogicIndexer
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.rules.toScalableVector
import edu.kit.ifv.populationsynthesis.utils.EquivalenceClass
import edu.kit.ifv.populationsynthesis.utils.formEquivalenceClass
import edu.kit.ifv.populationsynthesis.utils.log

class EquivalenceClassIPU<AREA, T>(
    ruleProvider: HierarchicRuleProvider<AREA, T>,
    seedHouseholds: Collection<T>,
    ipu: GenericIPU = GenericIPU.Companion.legacy,
) : HistoricIPU<AREA, T>(
    ruleProvider,
    seedHouseholds,
    ipu,
) {

    private lateinit var equivalenceClasses: EquivalenceClass<ScalableVector, T>
    override fun spawnVectorsFrom(indexedRules: Set<IndexedLogic<T>>): Collection<ScalableVector> {
        equivalenceClasses = seedHouseholds.associateWith { indexedRules.toScalableVector(it) }.formEquivalenceClass()
        return equivalenceClasses.representatives
    }

    override fun toElementRepresentations(vectors: ScalableVector): List<T> {
        return TODO()
    }

//    override fun generateScalableVectors(area: AREA): Pair<Collection<ScalableVector>, Collection<RuleObserver>> {
//        val parents = hierarchy.getAllAncestors(area)
//
//        val ruleObserverBuilder = RuleObserverBuilder(ruleProvider)
//        val indexedRules = ruleObserverBuilder.getLogics(parents + area)
//
//        val equivalenceClasses = seedHouseholds.associateWith { indexedRules.toScalableVector(it) }.formEquivalenceClass()
//        val observers = ruleObserverBuilder.buildFromVectors(area, equivalenceClasses.representatives)
//        return equivalenceClasses.representatives to observers
//
//    }
}

