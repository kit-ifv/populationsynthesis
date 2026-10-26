package edu.kit.ifv.populationsynthesis.algorithms.sampling

/**
 * Defines a strategy for sampling elements from multiple groups.
 *
 * The receiver map represents groups identified by [K], with each group containing
 * a collection of candidate elements of type [V]. Implementations decide how many
 * elements to select from each group and how the selection is performed.
 *
 * The resulting elements from all groups are combined into a single list.
 *
 * @param K type used to identify a group
 * @param V type of the elements contained in each group
 */
fun interface GroupedSampler<K, V> {
    fun Map<K, List<V>>.sample(): List<V>

    fun extract(map: Map<K, List<V>>) = map.sample()
}