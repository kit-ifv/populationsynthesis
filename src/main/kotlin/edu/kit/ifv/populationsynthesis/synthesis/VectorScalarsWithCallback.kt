package edu.kit.ifv.populationsynthesis.synthesis

import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.SparseScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.SparseVector

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