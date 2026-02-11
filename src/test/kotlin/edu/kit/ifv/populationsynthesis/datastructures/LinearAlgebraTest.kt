package edu.kit.ifv.populationsynthesis.datastructures

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class LinearAlgebraTest {

    @Test
    fun `matrix get(row,col) and get(row) work`() {
        val A = Matrix.instantiate(
            listOf(
                listOf(1.0, 2.0, 3.0),
                listOf(4.0, 5.0, 6.0)
            )
        )

        // get(row, col)
        assertEquals(1.0, A[0, 0], 1e-12)
        assertEquals(3.0, A[0, 2], 1e-12)
        assertEquals(4.0, A[1, 0], 1e-12)
        assertEquals(6.0, A[1, 2], 1e-12)

        // get(row)
        assertArrayEquals(doubleArrayOf(1.0, 2.0, 3.0), A[0], 1e-12)
        assertArrayEquals(doubleArrayOf(4.0, 5.0, 6.0), A[1], 1e-12)
    }

    @Test
    fun `matrix times vector works`() {
        // A = [ [1 2 3],
        //       [4 5 6] ]
        val A = Matrix.instantiate(
            listOf(
                listOf(1.0, 2.0, 3.0),
                listOf(4.0, 5.0, 6.0)
            )
        )
        val x = Vector(doubleArrayOf(10.0, 20.0, 30.0))

        // Expected:
        // y0 = 1*10 + 2*20 + 3*30 = 10 + 40 + 90 = 140
        // y1 = 4*10 + 5*20 + 6*30 = 40 + 100 + 180 = 320
        val y = A * x
        assertArrayEquals(doubleArrayOf(140.0, 320.0), y.content, 1e-12)
    }

    @Test
    fun `vector norm works`() {
        val v = Vector(doubleArrayOf(3.0, 4.0))
        assertEquals(5.0, v.norm(), 1e-12)

        val w = Vector(doubleArrayOf(1.0, 2.0, 2.0))
        assertEquals(3.0, w.norm(), 1e-12) // sqrt(1+4+4)=3
    }

    @Test
    fun `vector dot and scalar multiply work`() {
        val a = Vector(doubleArrayOf(1.0, 2.0, 3.0))
        val b = Vector(doubleArrayOf(4.0, -5.0, 6.0))

        // dot = 1*4 + 2*(-5) + 3*6 = 4 - 10 + 18 = 12
        assertEquals(12.0, a dot b, 1e-12)

        val c = a * 2.0
        assertArrayEquals(doubleArrayOf(2.0, 4.0, 6.0), c.content, 1e-12)
    }

    @Test
    fun `vector plus and minus work`() {
        val a = Vector(doubleArrayOf(1.0, 2.0, 3.0))
        val b = Vector(doubleArrayOf(10.0, 20.0, 30.0))

        val sum = a + b
        assertArrayEquals(doubleArrayOf(11.0, 22.0, 33.0), sum.content, 1e-12)

        val diff = b - a
        assertArrayEquals(doubleArrayOf(9.0, 18.0, 27.0), diff.content, 1e-12)
    }

    @Test
    fun `dimension mismatch throws`() {
        val A = Matrix.instantiate(
            listOf(
                listOf(1.0, 2.0),
                listOf(3.0, 4.0)
            )
        )
        val xWrong = Vector(doubleArrayOf(1.0, 2.0, 3.0))

        val ex = assertThrows(IllegalArgumentException::class.java) {
            A * xWrong
        }
        assertTrue(ex.message!!.contains("Dimension mismatch"))
    }
}