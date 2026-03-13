package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU
import edu.kit.ifv.populationsynthesis.rules.IndexedLogic
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.rules.toScalableVector
import edu.kit.ifv.populationsynthesis.utils.EquivalenceClass
import edu.kit.ifv.populationsynthesis.utils.formEquivalenceClass

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
    override fun spawnVectorsFrom(indexedRules: Set<IndexedLogic<in T>>): Collection<ScalableVector> {


        equivalenceClasses = seedHouseholds.associateWith { indexedRules.sortedBy { it.index }.toScalableVector(it) }.formEquivalenceClass()
        return equivalenceClasses.representatives
    }

    override fun toElementRepresentations(vectors: ScalableVector): List<T> {
        return equivalenceClasses.getValue(vectors)
    }

}

