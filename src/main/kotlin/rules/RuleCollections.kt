package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.rules.contribution.Contribution

fun <T> Collection<Rule<T>>.toScalableVectorOld(element: T): ScalableVector {
    return map { it.logic }.toScalableVector(element)
}

fun <T> Collection<Contribution<T>>.toScalableVector(element: T): ScalableVector {
    return ScalableVector.createFromLogics(element, this)
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