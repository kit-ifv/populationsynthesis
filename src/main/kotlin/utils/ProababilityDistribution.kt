package edu.kit.ifv.populationsynthesis.utils

import kotlin.math.ln


interface ProbabilityDistribution<T> {

}

interface DiscreteProbabilityDistribution<T> : ProbabilityDistribution<T>, Iterable<Probability> {
    val size: Int
    fun probabilityOfType(value: T): Probability = probabilityOf(mapIndex(value))
    fun probabilityOf(index: Int): Probability
    fun mapIndex(value: T): Int
    fun kullbackLeibler(other: DiscreteProbabilityDistribution<T>): Double {
        require(size == other.size) {
            "Theoretically we should also check for element equality"
        }
        val firstSum = this.zip(other).sumOf { (p, q) ->
            p * ln(p / q)
        }

        return firstSum
    }
}

inline fun <T> Iterable<T>.sumODf(selector: (T) -> Probability): Double {
    return map(selector).sumOf { it.probability }
}
class NumericProbDist(private val probabilities: Array<Probability>): DiscreteProbabilityDistribution<Int> {

    constructor(doubles: Collection<Double>): this(doubles.map { it.asProbability() }.toTypedArray())
    override val size: Int = probabilities.size
    override fun probabilityOf(index: Int): Probability {
        return probabilities[index]
    }
    override fun mapIndex(value: Int): Int = value
    override fun iterator(): Iterator<Probability> {
        return probabilities.iterator()
    }

    companion object {
        fun build(observedValues: Collection<Number>): NumericProbDist {
            val targets = observedValues.map{it.toDouble()}
            val sum = targets.sum()

            return NumericProbDist(targets.map { it / sum })
        }

        fun build(vararg observations: Number): NumericProbDist {
            return NumericProbDist.build(observations.toList())
        }
    }
}

class ArrayProbabilityDistribution<T> internal constructor(
    private val probabilities: Array<Probability>,
    private val mapper: Map<T, Int>
) : DiscreteProbabilityDistribution<T>, Iterable<Probability> {



     override fun probabilityOfType(value: T): Probability {
        return probabilities[mapIndex(value)]
    }

    override fun mapIndex(value: T): Int {
        return mapper.getValue(value)
    }

    override fun probabilityOf(index: Int): Probability {
        return probabilities[index]
    }
    override val size: Int = probabilities.size
    override fun iterator(): Iterator<Probability> {
        return probabilities.iterator()
    }


}

class ContinuousProbabilityDistribution<T : Comparable<T>>(private val cumulativeDistributionFunction: (T) -> Double) {
    fun target(range: ClosedRange<T>): Probability {
        return Probability.of(cumulativeDistributionFunction(range.endInclusive) - cumulativeDistributionFunction(range.start))
    }
}

