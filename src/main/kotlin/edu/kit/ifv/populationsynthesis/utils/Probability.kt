package edu.kit.ifv.populationsynthesis.utils

import kotlin.math.ln


@JvmInline
value class Probability internal constructor(val probability: Double) : Comparable<Probability> {

    val logProbability: Double get() = ln(probability)
    operator fun not(): Probability {
        return Probability.of(1.0 - probability)
    }

    operator fun times(other: Probability): Probability {
        return Probability(probability * other.probability)
    }

    operator fun times(other: Double): Double {
        return probability * other
    }

    operator fun div(other: Probability): Double {
        return probability / other.probability
    }

    override fun compareTo(other: Probability): Int {
        return probability.compareTo(other.probability)
    }

    companion object {
        fun of(value: Double): Probability {
            require(value in 0.0..1.0) {
                "Value must be between 0.0 and 1.0 inclusive"
            }
            return Probability(value)
        }


    }
}

fun log(probability: Probability): Probability {
    return Probability(probability.logProbability)
}

fun Double.asProbability(): Probability {
    return Probability.of(this)
}

