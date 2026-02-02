package edu.kit.ifv.populationsynthesis.algorithms

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class ScalableVectorTest {

    @Test
    fun noEqualityBetweenReferences() {
        val vecA = ScalableVector(listOf(1.0, 2.0, 3.0))
        val vecB = ScalableVector(listOf(1.0, 2.0, 3.0))
        assertNotEquals(vecA, vecB)
    }

}