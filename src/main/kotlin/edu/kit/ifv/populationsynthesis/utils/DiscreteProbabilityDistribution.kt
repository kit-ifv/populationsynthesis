package edu.kit.ifv.populationsynthesis.utils

import kotlin.collections.sumOf
import kotlin.math.ln

interface DiscreteProbabilityDistribution<T> {
    val size: Int get() = support.size
    val support: Collection<T>

    operator fun get(element: T): Probability
    fun kullbackLeiblerDivergence(other: DiscreteProbabilityDistribution<T>): Double {
        require(support.toSet() == other.support.toSet()) {
            "Distributions must have the same support"
        }

        return support.sumOf { element ->
            val p = this[element]
            val q = other[element]

            p * ln(p / q)
        }
    }
}