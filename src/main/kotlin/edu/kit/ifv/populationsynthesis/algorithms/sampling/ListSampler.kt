package edu.kit.ifv.populationsynthesis.algorithms.sampling

interface ListSampler {
    fun <T> sample(elements: List<T>, amount: Int): List<T>

}

