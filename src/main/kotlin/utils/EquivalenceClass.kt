package edu.kit.ifv.populationsynthesis.utils

interface EquivalenceClass<R, H>{
    val representatives: Set<R>
    fun elements(repr: R): Collection<H>
}

fun <K, V> Map<K, V>.formEquivalenceClass(): EquivalenceClass<V, K> = MapBasedEquivalenceClass(invertMap())

class MapBasedEquivalenceClass<R, H>(private val map: Map<R, List<H>>): EquivalenceClass<R, H>{
    override val representatives: Set<R> = map.keys
    override fun elements(repr: R): Collection<H> {
        return map[repr] ?: emptyList()
    }
}