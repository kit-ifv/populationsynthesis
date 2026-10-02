package edu.kit.ifv.populationsynthesis.synthesis

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.SparseScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.SparseVector
import edu.kit.ifv.populationsynthesis.rules.LogicIndexer
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider



fun interface MatrixTaskProvider<AREA, T> {
    fun provideProblemFor(area: AREA): MatrixTask
    operator fun get(area: AREA) = provideProblemFor(area)
}

class ResourceTaskProvider<AREA, T>(
    private val seedElements: Collection<T>,
    val ruleProvider: RuleProvider<AREA, in T>,
    val scalarInitializer: InitializeVectorScalars<T> = InitializeVectorScalars.byNormalizedElementCount
): MatrixTaskProvider<AREA, T> {
    override fun provideProblemFor(area: AREA): MatrixTask {
        val rules = ruleProvider[area]
        if(rules.isEmpty()) return MatrixTask.EMPTY
        val logicIndexer = LogicIndexer.fromProvider(ruleProvider)
        val entries = seedElements.groupBy { logicIndexer.createSignature(it) } as Map<SparseVector, List<T>>
        val vectors = scalarInitializer.generateScalars(entries)
        val observers = logicIndexer.indexRules(rules).map { (index, rule) ->
            RuleObserver.fromRule(rule, index, vectors)
        }
        return MatrixTask(vectors, observers)
    }
}

fun interface InitializeVectorScalars<in T> {
    fun generateScalars(vectorElementMapping: Map<SparseVector, List<T>>): List<SparseScalableVector>

    companion object {
        val byNormalizedElementCount = InitializeVectorScalars<Any?> {vectorElementMapping ->
            val totalSize = vectorElementMapping.values.sumOf { it.size }
            vectorElementMapping.entries.map { (vector, elements) ->
                SparseScalableVector(vector, elements.size.toDouble() / totalSize)
            }
        }
    }
}

data class MatrixTask(
    val vectors: Collection<ScalableVector>,
    val observers: Collection<RuleObserver>,
) {
    companion object {
        val EMPTY = MatrixTask(emptyList(), emptyList())
    }
}