package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector

fun <T> Collection<Rule<T>>.toScalableVector(element: T): ScalableVector {
    return ScalableVector.createFrom(element, this)
}

fun <H> Collection<Rule<H>>.fuse(): Rule<H> {
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
