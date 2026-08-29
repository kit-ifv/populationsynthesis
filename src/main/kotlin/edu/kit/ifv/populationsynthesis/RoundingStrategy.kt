package edu.kit.ifv.populationsynthesis

import edu.kit.ifv.populationsynthesis.algorithms.WeightedElement
import edu.kit.ifv.populationsynthesis.algorithms.IntegerWeightedElement

fun interface RoundingStrategy {
    fun convertToInts(values: Collection<Double>): List<Int>

    fun <I> integerizeMatrixOutput(elements: Collection<WeightedElement<I>>): List<IntegerWeightedElement<I>> {
        val newValues = convertToInts(elements.map { it.amount })
        return elements.zip(newValues).map { (e, num) -> e.discretize(num) }
    }
}