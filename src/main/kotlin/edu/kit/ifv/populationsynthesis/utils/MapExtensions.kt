package edu.kit.ifv.populationsynthesis.utils

fun <K, V> Map<K, V>.invertMap(): Map<V, List<K>> {
    return this.entries
        .groupBy({ it.value }, { it.key })
}

fun <K, V> Map<K, V>.partitionValues(predicate: (V) -> Boolean): Pair<Map<K, V>, Map<K, V>> {
    val matching = mutableMapOf<K, V>()
    val nonMatching = mutableMapOf<K, V>()

    for ((key, value) in this) {
        if (predicate(value)) {
            matching[key] = value
        } else {
            nonMatching[key] = value
        }
    }

    return matching to nonMatching
}