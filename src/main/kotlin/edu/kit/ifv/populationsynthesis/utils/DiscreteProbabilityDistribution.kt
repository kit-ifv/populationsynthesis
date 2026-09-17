package edu.kit.ifv.populationsynthesis.utils

import java.lang.Math.abs
import java.lang.Math.exp
import java.lang.Math.sqrt
import kotlin.collections.sumOf
import kotlin.math.ln

interface DiscreteProbabilityDistribution<T> {
    val size: Int get() = support.size
    val support: Collection<T>

    operator fun get(element: T): Probability
    fun kullbackLeiblerDivergence(other: DiscreteProbabilityDistribution<T>): Double {
        requireSameSupport(other)

        return support.sumOf { element ->
            val p = this[element]
            val q = other[element]

            p * ln(p / q)
        }
    }

    private fun requireSameSupport(other: DiscreteProbabilityDistribution<T>) {
        require(support.toSet() == other.support.toSet()) {
            "Distributions must have the same support"
        }
    }

    fun jensenShannonDivergence(
        other: DiscreteProbabilityDistribution<T>
    ): Double {
        requireSameSupport(other)

        return support.sumOf { element ->
            val p = this[element].toDouble()
            val q = other[element].toDouble()
            val m = (p + q) / 2.0

            val pTerm =
                if (p == 0.0) 0.0
                else p * ln(p / m)

            val qTerm =
                if (q == 0.0) 0.0
                else q * ln(q / m)

            (pTerm + qTerm) / 2.0
        }
    }

    fun totalVariationDistance(
        other: DiscreteProbabilityDistribution<T>
    ): Double {
        requireSameSupport(other)

        return support.sumOf { element ->
            kotlin.math.abs(this[element].toDouble() - other[element].toDouble())
        } / 2.0
    }

    fun l1Distance(
        other: DiscreteProbabilityDistribution<T>
    ): Double {
        requireSameSupport(other)

        return support.sumOf { element ->
            abs(this[element].toDouble() - other[element].toDouble())
        }
    }

    fun l2Distance(
        other: DiscreteProbabilityDistribution<T>
    ): Double {
        requireSameSupport(other)

        return sqrt(
            support.sumOf { element ->
                val difference = this[element].toDouble() - other[element].toDouble()
                difference * difference
            }
        )
    }

    fun hellingerDistance(
        other: DiscreteProbabilityDistribution<T>
    ): Double {
        requireSameSupport(other)

        return kotlin.math.sqrt(
            support.sumOf { element ->
                val difference =
                    sqrt(this[element].toDouble()) - sqrt(other[element].toDouble())

                difference * difference
            } / 2.0
        )
    }

    fun cosineSimilarity(
        other: DiscreteProbabilityDistribution<T>
    ): Double {
        requireSameSupport(other)

        val dot = support.sumOf { element ->
            this[element].toDouble() * other[element].toDouble()
        }

        val normThis = sqrt(
            support.sumOf { element ->
                val p = this[element].toDouble()
                p * p
            }
        )

        val normOther = sqrt(
            support.sumOf { element ->
                val p = other[element].toDouble()
                p * p
            }
        )

        if (normThis == 0.0 || normOther == 0.0) {
            return 0.0
        }

        return dot / (normThis * normOther)
    }

    fun entropy(): Double =
        support.sumOf { element ->
            val p = this[element].toDouble()

            if (p == 0.0) {
                0.0
            } else {
                -p * ln(p)
            }
        }

    fun effectiveSupportSize(): Double =
        exp(entropy())

    fun nonZeroCount(): Int =
        support.count { this[it].toDouble() > 0.0 }

    fun maximumProbability(): Double =
        support.maxOf { this[it].toDouble() }

    fun minimumNonZeroProbability(): Double? =
        support
            .map { this[it].toDouble() }
            .filter { it > 0.0 }
            .minOrNull()

    fun concentration(): Double =
        support.sumOf { element ->
            val p = this[element].toDouble()
            p * p
        }

    fun inverseConcentration(): Double {
        val concentration = concentration()

        return if (concentration == 0.0) {
            0.0
        } else {
            1.0 / concentration
        }
    }
}