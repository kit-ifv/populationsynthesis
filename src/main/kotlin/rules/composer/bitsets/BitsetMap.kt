package edu.kit.ifv.populationsynthesis.rules.composer.bitsets

import java.util.*

interface BitsetMap<T> {

    operator fun get(element: T, index: Int): Boolean
    operator fun get(area: T): BitSet
    fun isFlagged(element: T, index: Int): Boolean = get(element, index)

    fun allAreFlagged(element: Collection<T>, index: Int): Boolean

    fun andConjunction(element: Collection<T>): BitSet
}