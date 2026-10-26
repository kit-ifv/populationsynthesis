package edu.kit.ifv.populationsynthesis.utils

import kotlin.math.exp
import kotlin.math.ln

@JvmInline
value class LogProbability internal constructor(val logProbability: Double) : Comparable<Probability> {

    val value: Double get() = exp(logProbability)
    operator fun not(): Probability {
        return Probability.of(1.0 - value)
    }

    operator fun times(other: Probability): Probability {
        return Probability(logProbability + other.logProbability)
    }


    override fun compareTo(other: Probability): Int {
        return logProbability.compareTo(other.logProbability)
    }

    override fun toString(): String {
        return "Probability(value=$value, ll=$logProbability)"
    }

    companion object {
        fun of(value: Double): Probability {
            require(value in 0.0..1.0) {
                "Value must be between 0.0 and 1.0 inclusive"
            }
            return Probability(ln(value))
        }


    }
}