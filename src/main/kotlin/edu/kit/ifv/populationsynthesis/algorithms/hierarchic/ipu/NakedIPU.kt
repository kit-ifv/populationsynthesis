package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU
import edu.kit.ifv.populationsynthesis.rules.IndexedLogic
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.rules.toScalableVector
import java.util.*

class NakedIPU<AREA, T>(
    ruleProvider: HierarchicRuleProvider<AREA, T>,
    seedHouseholds: Collection<T>,
    ipu: GenericIPU = GenericIPU.Companion.legacy,
) : HistoricIPU<AREA, T>(
    ruleProvider,
    seedHouseholds,
    ipu,

    ) {

    private val vectorMapping: MutableMap<ScalableVector, T> = IdentityHashMap()
    override fun spawnVectorsFrom(indexedRules: Set<IndexedLogic<in T>>): Collection<ScalableVector> {
        val vectors = seedHouseholds.map { seed ->
            indexedRules.toScalableVector(seed).also { vectorMapping[it] = seed }

        }

        return vectors
    }

    override fun toElementRepresentations(vectors: ScalableVector): List<T> {
        val element = vectorMapping[vectors]
            ?: throw NoSuchElementException("There is no vector $vectors present in the map, thus it cannot be reconstructed to a T element")
        return listOf(element)
    }

}