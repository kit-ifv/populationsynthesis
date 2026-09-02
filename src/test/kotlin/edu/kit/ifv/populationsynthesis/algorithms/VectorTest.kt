package edu.kit.ifv.populationsynthesis.algorithms

import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class VectorTest  {
    private val a = _root_ide_package_.edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector(1.0, 2.0)
    private val b = _root_ide_package_.edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector(1.0, 2.0, 1.0)

    private val c = _root_ide_package_.edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector(.0, .0, .0, .0)
    @Test
    fun addition() {
        assertTrue((a + b).contentEquals(
            _root_ide_package_.edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector(
                2.0,
                4.0,
                1.0
            )
        ))
        assertTrue((b + a).contentEquals(
            _root_ide_package_.edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector(
                2.0,
                4.0,
                1.0
            )
        ))
        assertTrue((a - b).contentEquals(
            _root_ide_package_.edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector(
                .0,
                .0,
                -1.0
            )
        ))
        assertTrue((b - a).contentEquals(
            _root_ide_package_.edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector(
                .0,
                .0,
                1.0
            )
        ))

        assertTrue((a + c).contentEquals(a))
        assertTrue((a - c).contentEquals(a))
        assertTrue((b + c).contentEquals(b))
        assertTrue((b - c).contentEquals(b))

    }

    @Test
    fun lengths() {
        assertEquals(a.length(), sqrt(5.0) )
        assertEquals(b.length(), sqrt(6.0) )
        assertEquals(c.length(), sqrt(.0) )
    }

    @Test
    fun trivialCosineSimilarity() {
        assertEquals(a.cosineSimilarity(a), 1.0, 0.00000001)
        assertEquals(b.cosineSimilarity(b), 1.0, 0.00000001)
    }
    @Test
    fun cosineNaNs() {
        assertEquals(a.cosineSimilarity(c), Double.NaN)
        assertEquals(b.cosineSimilarity(c), Double.NaN)
        assertEquals(c.cosineSimilarity(c), Double.NaN)

        assertEquals(c.cosineSimilarity(a), Double.NaN)
        assertEquals(c.cosineSimilarity(b), Double.NaN)
        assertEquals(c.cosineSimilarity(c), Double.NaN)
    }

    @Test
    fun chattyCalculations() {
        val a = _root_ide_package_.edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector(0.0, 0.1)
        val b = _root_ide_package_.edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector(1.0, 0.9)
        val target = _root_ide_package_.edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector(sqrt(2.0), .0)
        val p = a + b
        val r = target - p
        assertTrue(p.contentEquals(
            _root_ide_package_.edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector(
                1.0,
                1.0
            )
        ))
        assertTrue(r.contentEquals(
            _root_ide_package_.edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector(
                sqrt(
                    2.0
                ) - 1, -1.0
            )
        ))
        val cosineA = a.cosineSimilarity(r)
        val cosineB = b.cosineSimilarity(r)

        val optA = 1 + (r.length() * cosineA)
        val optB = 1 + (r.length() * cosineB)

        println(r.length())

        println("DONE")

    }
}