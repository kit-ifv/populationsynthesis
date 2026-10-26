package edu.kit.ifv.populationsynthesis.utils

interface IndexedProbabilityDistribution<T> :
    DiscreteProbabilityDistribution<T> {

    override val support: List<T>

    fun probabilityAt(index: Int): Probability
}