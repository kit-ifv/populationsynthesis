package edu.kit.ifv.populationsynthesis.synthesis

import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.SparseScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.SparseVector

/**
 * Main use case: We have a sparse vector as representative for a collection of elements T, and we need to generate an
 * initial scalar as starting value for the matrix problem. So implementors can chnage the starting value of this
 * behaviour.
 */
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

fun <T> InitializeVectorScalars<T>.withCallback(lambda: (List<ScalableVector>) -> Unit): InitializeVectorScalars<T> {
    return VectorScalarsWithCallback(this, lambda)
}

class VectorScalarsWithCallback<in T>(
    private val original: InitializeVectorScalars<T>,
    private val callback: (List<ScalableVector>) -> Unit
): InitializeVectorScalars<T> {
    override fun generateScalars(vectorElementMapping: Map<SparseVector, List<T>>): List<SparseScalableVector> {
        val output = original.generateScalars(vectorElementMapping)
        callback(output)
        return output
    }
}