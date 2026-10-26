package edu.kit.ifv.populationsynthesis.datastructures.linalg

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.math.sqrt
import kotlin.test.assertContentEquals

class SparseVectorTest {

    @Test
    fun sparseIterator() {
        val v = SparseVector.fromValues(1, 0, 2, 3, 4, 5, 0, 1, 0, 0 ,0)
        val accumulator = mutableListOf<Double>()
        v.iterator().forEach { accumulator.add(it) }
        val indexAccumulator = mutableListOf<Int>()
        v.nonzeroIndices.iterator().forEach { indexAccumulator.add(it) }
        assertContentEquals(listOf(0, 2, 3, 4, 5, 7), indexAccumulator)
        assertContentEquals(listOf(1.0, .0, 2.0, 3.0, 4.0, 5.0, .0, 1.0), accumulator)
    }

    @Test
    fun dotProduct() {

        val a = SparseVector.fromValues(2.0, 2.0)
        val b = SparseVector.fromValues(0.0, 1.0, 1.0)
        val c = SparseVector.fromValues(1.0)

        assertEquals(2.0, b.dot(a))

        assertEquals(2.0, a.dot(c))
        assertEquals(2.0, c.dot(a))
        assertEquals(2.0, a.dot(b))
        assertEquals(0.0, b.dot(c))
        assertEquals(0.0, c.dot(b))
    }

    @Test
    fun euclideanNormSquared() {
        val a = SparseVector.fromValues(1.0, 2.0)
        assertEquals(5.0, a.euclideanNormSquared())
        assertEquals(sqrt(5.0), a.length())
    }

}