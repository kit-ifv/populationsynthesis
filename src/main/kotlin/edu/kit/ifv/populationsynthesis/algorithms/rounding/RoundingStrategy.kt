package edu.kit.ifv.populationsynthesis.algorithms.rounding

import edu.kit.ifv.populationsynthesis.algorithms.IntegerWeightedElement
import edu.kit.ifv.populationsynthesis.algorithms.WeightedElement

fun interface RoundingStrategy {
    fun convertToInts(values: List<Double>): List<Int>

    fun <I> integerizeMatrixOutput(elements: List<WeightedElement<I>>): List<IntegerWeightedElement<I>> {
        val newValues = convertToInts(elements.map { it.amount })
        return elements.zip(newValues).map { (e, num) -> e.discretize(num) }
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