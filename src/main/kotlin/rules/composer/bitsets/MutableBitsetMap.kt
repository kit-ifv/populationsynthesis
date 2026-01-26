package edu.kit.ifv.populationsynthesis.rules.composer.bitsets

import java.util.BitSet


class MutableBitsetMap<T> private constructor(private val map: MutableMap<T, BitSet>) : BitsetMap<T> {

    constructor() : this(mutableMapOf())

    override operator fun get(element: T, index: Int): Boolean {
        val activeBitset = map[element] ?: return false
        return activeBitset.get(index)
    }
    override operator fun get(area: T): BitSet = map.getValue(area)

    fun getOrPut(element: T, default: () ->BitSet = { BitSet() }): BitSet {
        return map.getOrPut(element, default)
    }

    operator fun set(element: T, bitset: BitSet) {
        map[element] = bitset
    }


    override fun allAreFlagged(element: Collection<T>, index: Int): Boolean {
        return element.all { isFlagged(it, index) }
    }

    override fun andConjunction(element: Collection<T>): BitSet {
        val bitsets = element.map { map.getValue(it) }
        return bitsets.andAll()
    }

    private fun Collection<BitSet>.andAll(): BitSet {
        if(isEmpty()) return BitSet() // Empty bitset for an empty collection.

        val first = first().clone() as BitSet
        drop(1).forEach {
            first.and(it)
        }
        return first
    }

}

