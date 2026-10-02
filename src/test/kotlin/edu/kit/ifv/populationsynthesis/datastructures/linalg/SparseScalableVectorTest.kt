package edu.kit.ifv.populationsynthesis.datastructures.linalg

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SparseScalableVectorTest {
    @Test
    fun iteratorWorks() {
        val vector = SparseScalableVector(1.0, 1, 0 , 20, 30, 0)
        val accumulator= mutableListOf<Double>()
        vector.iterator().forEach {
            accumulator.add(it)
        }
        assertEquals(1.0, accumulator[0])
        assertEquals(.0, accumulator[1])
        assertEquals(20.0, accumulator[2])
        assertEquals(30.0, accumulator[3])
        assertEquals(4, accumulator.size)

        accumulator.clear()
        vector.scalar = 2.0
        vector.iterator().forEach {
            accumulator.add(it)
        }
        assertEquals(2.0, accumulator[0])
        assertEquals(.0, accumulator[1])
        assertEquals(40.0, accumulator[2])
        assertEquals(60.0, accumulator[3])
    }
}