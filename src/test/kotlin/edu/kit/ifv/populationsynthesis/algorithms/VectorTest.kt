package edu.kit.ifv.populationsynthesis.algorithms

import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class VectorTest {
    private val a = Vector(1.0, 2.0)
    private val b = Vector(1.0, 2.0, 1.0)

    private val c = Vector(.0, .0, .0, .0)

    @Test
    fun addition() {
        assertTrue(
            (a + b).contentEquals(
                Vector(
                    2.0,
                    4.0,
                    1.0
                )
            )
        )
        assertTrue(
            (b + a).contentEquals(
                Vector(
                    2.0,
                    4.0,
                    1.0
                )
            )
        )
        assertTrue(
            (a - b).contentEquals(
                Vector(
                    .0,
                    .0,
                    -1.0
                )
            )
        )
        assertTrue(
            (b - a).contentEquals(
                Vector(
                    .0,
                    .0,
                    1.0
                )
            )
        )

        assertTrue((a + c).contentEquals(a))
        assertTrue((a - c).contentEquals(a))
        assertTrue((b + c).contentEquals(b))
        assertTrue((b - c).contentEquals(b))

    }

    @Test
    fun lengths() {
        assertEquals(a.length(), sqrt(5.0))
        assertEquals(b.length(), sqrt(6.0))
        assertEquals(c.length(), sqrt(.0))
    }

    @Test
    fun trivialCosineSimilarity() {
        assertEquals(1.0, a.cosineSimilarity(a), 0.00000001)
        assertEquals(1.0, b.cosineSimilarity(b), 0.00000001)
    }

    @Test
    fun cosineNaNs() {
        assertEquals(Double.NaN, a.cosineSimilarity(c))
        assertEquals(Double.NaN, b.cosineSimilarity(c))
        assertEquals(Double.NaN, c.cosineSimilarity(c))

        assertEquals(Double.NaN, c.cosineSimilarity(a))
        assertEquals(Double.NaN, c.cosineSimilarity(b))
        assertEquals(Double.NaN, c.cosineSimilarity(c))
    }
    @Test
    fun safeCosines() {
        assertEquals(.0, c.safeCosineSimilarity(a))
        assertEquals(1.0, a.cosineSimilarity(a), 0.00000001)
    }
}