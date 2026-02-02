package edu.kit.ifv.populationsynthesis.utils

interface EquivalenceClass<R, T> : Map<R, List<T>> {
    val representatives: Set<R>
    fun elements(repr: R): Collection<T>

}

interface MutableEquivalenceClass<R, H> : EquivalenceClass<R, H> {
    fun add(element: H)
    fun remove(element: H)
}

fun <K, V> Map<K, V>.formEquivalenceClass(): EquivalenceClass<V, K> = MapBasedEquivalenceClass(invertMap())

class MapBasedEquivalenceClass<R, T>(private val map: Map<R, List<T>>) : MutableEquivalenceClass<R, T>,
    Map<R, List<T>> by map {
    override val representatives: Set<R> = map.keys
    override fun elements(repr: R): Collection<T> {
        return map[repr] ?: emptyList()
    }

    override fun add(element: T) {
        TODO("Not yet implemented")
    }

    override fun remove(element: T) {
        TODO("Not yet implemented")
    }
}