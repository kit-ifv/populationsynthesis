package edu.kit.ifv.populationsynthesis.utils

class NumericProbabilityDistribution private constructor(
    private val probabilities: Array<Probability>
) : IndexedProbabilityDistribution<Int> {

    override val support: List<Int> =
        probabilities.indices.toList()

    override operator fun get(element: Int): Probability =
        probabilities[element]

    override fun probabilityAt(index: Int): Probability =
        probabilities[index]

    companion object {

        fun fromWeights(weights: Collection<Number>): NumericProbabilityDistribution {
            val values = weights.map{it.toDouble().coerceAtLeast(0.0)}
            val sum = values.sum()

            require(sum > 0.0)

            return NumericProbabilityDistribution(
                values
                    .map { (it / sum).asProbability() }
                    .toTypedArray()
            )
        }

        fun fromWeights(vararg weights: Number) =
            fromWeights(weights.asList())
    }
}