package edu.kit.ifv.populationsynthesis.datastructures.linalg

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DenseVectorTest {


    private val emptyVector: DenseVector = DenseVector(doubleArrayOf())


    @Test
    fun convenienceConstructor() {
        val a = DenseVector(1.0, 2, 3, 0, 4)
        assertEquals(5, a.size)
        assertEquals(1.0, a[0])
        assertEquals(2.0, a[1])
        assertEquals(3.0, a[2])
        assertEquals(0.0, a[3])
        assertEquals(4.0, a[4])
        assertEquals(0.0, a[5])
        assertEquals(0.0, a[6])
        assertEquals(0.0, a[-1])


    }
    @Test
    fun inplaceSubtraction() {
        val a = DenseVector(1.0, 2, 3, 0, 4)
        a -= Vector(.0, 1.0, 3.0, -4.0)

        assertEquals(1.0, a[0])
        assertEquals(1.0, a[1])
        assertEquals(.0, a[2])
        assertEquals(4.0, a[3])
        assertEquals(4.0, a[4])
    }

    @Test
    fun inplaceAddition() {

        val a = DenseVector(1.0, 20, 30)
        a += a

        assertEquals(2.0, a[0])
        assertEquals(40.0, a[1])
        assertEquals(60.0, a[2])
    }

    @Test
    fun addingEmptyVector() {
        val a = DenseVector(1.0, 20, 30)

        val b = a + emptyVector
        assertTrue(b.contentEquals(a))
        val c = emptyVector + a
        assertTrue(c.contentEquals(a))
    }

    @Test
    fun unaryMinus() {
        val a = DenseVector(1.0, 20, 30)
        val b = -a
        assertTrue(b.contentEquals(listOf(-1.0, -20.0, -30.0)))
    }

    @Test
    fun dotProduct() {

        val a = Vector(2.0, 2.0)
        val b = Vector(0.0, 1.0, 1.0)
        val c = Vector(1.0)


        assertEquals(2.0, a.dot(c))
        assertEquals(2.0, c.dot(a))
        assertEquals(2.0, a.dot(b))
        assertEquals(2.0, b.dot(a))
        assertEquals(0.0, b.dot(c))
        assertEquals(0.0, c.dot(b))


    }

}