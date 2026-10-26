package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.SparseScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.SparseVector
import edu.kit.ifv.populationsynthesis.rules.measurement.Measurement


fun <T> Collection<Measurement<T>>.toScalableVectorX(element: T): SparseScalableVector {
    return ScalableVector.createFromLogics(element, this)
}

fun <T> Collection<Measurement<T>>.toSparseVector(element: T): SparseVector {
    return SparseVector.fromValues(map { it.measure(element) })
}

fun <T> Collection<Rule<T>>.toScalableVector(element: T): SparseScalableVector {
    return map { it.logic }.toScalableVectorX(element)
}



fun <T> Collection<Rule<T>>.sumRule(): Rule<T> {
    require(isNotEmpty()) {
        "Cannot fuse empty"
    }
    val logic = first().logic
    require(all { it.logic == logic }) {
        "Need to have same logic. "
    }

    val sum = sumOf { it.target }
    return Rule(sum, logic)
}

fun <T> Collection<Rule<T>>.delta(elements: Collection<T>): Map<Rule<T>, Double> {
    return associateWith { it.delta(elements) }
}

fun <T> Collection<Rule<T>>.total(elements: Collection<T>): Map<Rule<T>, Double> {
    return associateWith { it.total(elements) }
}

fun <T> Collection<Rule<T>>.relativeTotals(elements: Collection<T>): Map<Rule<T>, Double> {
    val size = elements.size
    return associateWith { it.total(elements) }.mapValues { it.value / size.toDouble() }
}