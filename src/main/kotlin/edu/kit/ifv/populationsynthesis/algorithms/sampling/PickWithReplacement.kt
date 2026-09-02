package edu.kit.ifv.populationsynthesis.algorithms.sampling

import edu.kit.ifv.populationsynthesis.utils.pickWithReplacement
import kotlin.random.Random

class PickWithReplacement(private val random: Random): ListSampler {
    override fun <T> sample(elements: List<T>, amount: Int): List<T> {
        return elements.pickWithReplacement(amount, random)
    }
}