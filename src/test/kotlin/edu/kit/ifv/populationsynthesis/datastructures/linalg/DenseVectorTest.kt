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

    @Test
    fun manualResidualCalculation() {
        val a = SparseVector.fromMap(
            mapOf(
                6 to 1.0,
                7 to 1.0,
                8 to 1.0,
                11 to 2.0,
                12 to 1.0,
                15 to 1.0,
                20 to 1.0,
                29 to 2.0,
                32 to 1.0,
                37 to 1.0
            )
        )
        val residual = DenseVector(
            doubleArrayOf(
                7.30913008393592,
                6.860085426993247,
                -56.70156790187259,
                -8.504350684024843,
                2.709523830801506,
                9.58200948837701,
                -36.86188337938864,
                -40.113130853460994,
                0.4584347843412042,
                14.3998826481264,
                16.569161808812222,
                -52.649054603060115,
                34.79733146258155,
                102.36364516312746,
                100.05941692269423,
                29.8067718765603,
                17.15562017206471,
                -10.935891345417955,
                89.39736990135908,
                27.56534841282498,
                132.4868444747988,
                -1.0060537408207875,
                14.60939456972105,
                163.31921978089363,
                13.043886054481858,
                3072.9229145266086,
                2700.5946561109076,
                -294.12893001025617,
                2874.9879202972966,
                -653.5080448900547,
                -1988.6310021394486,
                1550.066286192312,
                -2333.637952590201,
                -1951.7973059930416,
                -1462.377877297955,
                -763.456253797029,
                -1894.3933950726873,
                -1659.0092475615986,
                -927.8750828036453
            ),
        )

        val denominator = a.euclideanNormSquared()
        val result = (a.dot(residual) / denominator)
        assertEquals(-330.27418942328734, result, 0.0001)


    }
}