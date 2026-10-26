package edu.kit.ifv.populationsynthesis.algorithms.sampling

import edu.kit.ifv.populationsynthesis.utils.pickWithReplacement
import kotlin.random.Random

/**
 * Samples a determined number of elements from each group of a grouped collection.
 *
 * For every group identified by a key of type [K], [amountDeterminer] determines
 * how many elements should be sampled from that group. Sampling is performed
 * independently within each group and with replacement.
 *
 * The samples from all groups are combined into a single result list.
 *
 * @param K type identifying a group
 * @param V type of the elements being sampled
 * @property random random number generator used for sampling
 * @property amountDeterminer determines the number of elements to sample for each group
 */

open class AmountBasedGroupSampler<K, V>(
    val random: Random = Random(1),
    private val amountDeterminer: (Collection<K>) -> Map<K, Int>,
) : GroupedSampler<K, V> {
    override fun Map<K, List<V>>.sample(): List<V> {
        val amounts = amountDeterminer(keys)

        return flatMap { (key, elements) ->
            val amount = amounts.getValue(key)

            require(amount >= 0) {
                "Sample amount for group '$key' must not be negative, but was $amount"
            }

            elements.pickWithReplacement(amount, random)
        }
    }
}