package edu.kit.ifv.populationsynthesis.datastructures.linalg

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DenseVectorTest {

    @Test
    fun addDenseVector() {

        val a = DenseVector(1.0, 20, 30)


        a += a

        assertEquals(a[0], 2.0)
        assertEquals(a[1], 40.0)
        assertEquals(a[2], 60.0)
    }

}