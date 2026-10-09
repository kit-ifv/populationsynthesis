package edu.kit.ifv.populationsynthesis.algorithms.rounding

import edu.kit.ifv.populationsynthesis.algorithms.IntegerWeightedElement
import edu.kit.ifv.populationsynthesis.algorithms.WeightedElement
import edu.kit.ifv.populationsynthesis.datastructures.linalg.SparseVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.toSparseVector
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider
import edu.kit.ifv.populationsynthesis.synthesis.MatrixTask

fun interface RoundingStrategy {
    fun convertToInts(values: List<Double>): List<Int>

    fun integerizeMatrixOutput(elements: List<WeightedElement<SparseVector>>): List<IntegerWeightedElement<SparseVector>> {
        val newValues = convertToInts(elements.map { it.amount })
        return elements.zip(newValues).map { (e, num) -> e.discretize(num) }
    }

    fun integerizeMatrixTask(task: MatrixTask): List<IntegerWeightedElement<SparseVector>> {
        val (vectors, _) = task
        val roundedValues = convertToInts(vectors.map { it.scalar })
        return vectors.zip(roundedValues).map { (a, b) ->
            IntegerWeightedElement(a.baseVector.toSparseVector(), b)
        }
    }

    companion object {
        val cumulative = RoundingStrategy { values ->
            var remainder = 0.0
            values.map {
                val base = it.toInt()
                remainder += it - base
                val amount = if (remainder >= 1.0) {
                    remainder--
                    base + 1
                } else {
                    base
                }
                amount
            }
        }
    }
}
fun RoundingStrategy.withCallback(lambda: (List<Int>, MatrixTask) -> Unit): RoundingStategyWithCallback {
    return RoundingStategyWithCallback(this, lambda)
}
class RoundingStategyWithCallback(
    val original: RoundingStrategy,
    val callback: (List<Int>, MatrixTask) -> Unit,
) : RoundingStrategy {
    override fun convertToInts(values: List<Double>): List<Int> {
        return original.convertToInts(values)
    }

    override fun integerizeMatrixTask(task: MatrixTask): List<IntegerWeightedElement<SparseVector>> {
        val (vectors, _) = task
        val roundedValues = convertToInts(vectors.map { it.scalar })
        callback(roundedValues, task)
        return vectors.zip(roundedValues).map { (a, b) ->
            IntegerWeightedElement(a.baseVector.toSparseVector(), b)
        }
    }
}