package edu.kit.ifv.populationsynthesis.synthesis

import edu.kit.ifv.populationsynthesis.algorithms.WeightedElement
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.SparseVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.toSparseVector

/**
 * Wrapper class that holds the vectors and observers from the matrix problem Ax = t
 */
data class MatrixTask(
    val vectors: Collection<ScalableVector>,
    val observers: Collection<RuleObserver>,
) {

    fun extractSolution(): List<WeightedElement<SparseVector>> {
        return vectors.map { WeightedElement(it.baseVector.toSparseVector(), it.scalar) }
    }
    companion object {
        val EMPTY = MatrixTask(emptyList(), emptyList())
    }
}