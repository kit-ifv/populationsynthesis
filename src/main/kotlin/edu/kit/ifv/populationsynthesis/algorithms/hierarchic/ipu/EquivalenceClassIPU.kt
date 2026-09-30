package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.MatrixSolver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.toSparseVector
import edu.kit.ifv.populationsynthesis.datastructures.toSparseVector
import edu.kit.ifv.populationsynthesis.rules.IndexedLogic
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.rules.toScalableVector
import edu.kit.ifv.populationsynthesis.rules.toScalableVectorX
import edu.kit.ifv.populationsynthesis.rules.toSparseVector
import edu.kit.ifv.populationsynthesis.utils.EquivalenceClass
import edu.kit.ifv.populationsynthesis.utils.MapBasedEquivalenceClass
import edu.kit.ifv.populationsynthesis.utils.formEquivalenceClass
import edu.kit.ifv.populationsynthesis.utils.invertMap

class EquivalenceClassIPU<AREA, T>(
    ruleProvider: HierarchicRuleProvider<AREA, T>,
    seedHouseholds: Collection<T>,
    ipu: MatrixSolver = MatrixSolver.legacy,
) : HistoricIPU<AREA, T>(
    ruleProvider,
    seedHouseholds,
    ipu,
) {

    private lateinit var equivalenceClasses: EquivalenceClass<ScalableVector, T>
    override fun spawnVectorsFrom(indexedRules: Set<IndexedLogic<in T>>): Collection<ScalableVector> {


        val invertMap = seedHouseholds.associateWith { indexedRules.sortedBy { it.index }.toSparseVector(it) }
            .invertMap()
        equivalenceClasses = MapBasedEquivalenceClass(
            invertMap.mapKeys { it.key.toScalableVector()},
        )
        return equivalenceClasses.representatives
    }

    override fun toElementRepresentations(vectors: ScalableVector): List<T> {
        return equivalenceClasses.getValue(vectors)
    }

}

