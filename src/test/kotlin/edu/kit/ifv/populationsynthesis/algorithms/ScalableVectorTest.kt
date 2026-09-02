package edu.kit.ifv.populationsynthesis.algorithms

import edu.kit.ifv.populationsynthesis.datastructures.linalg.ArrayScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.sum
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class ScalableVectorTest {
    @Test

    fun scalableVector() {
        val vector = ArrayScalableVector(doubleArrayOf(3.0, 4.0,), 1.0)
        val comparison = _root_ide_package_.edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector(3.0, 4.0)
        assertEquals(5.0, vector.length())
        vector.scalar *= 2.0
        assertEquals(10.0, vector.length())

        assertEquals(6.0, vector[0])
        assertEquals(8.0, vector[1])

        assertTrue(listOf(vector, vector, vector).sum().contentEquals(
            _root_ide_package_.edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector(
                18.0,
                24.0
            )
        ))

    }
}