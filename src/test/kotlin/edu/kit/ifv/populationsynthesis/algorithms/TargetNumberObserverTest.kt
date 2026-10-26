package edu.kit.ifv.populationsynthesis.algorithms

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.TargetNumberObserver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.DenseScalableVector
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class TargetNumberObserverTest {

    @Test
    fun properBehaviour() {
        val logicIdentifier = "Fake Identifier"
        val mutatingVector = DenseScalableVector(1, 2, 3)
        val vectors = listOf(
            DenseScalableVector(1, 2, 3),
            mutatingVector,
            DenseScalableVector(1, 2, 3),
        )
        val obs = TargetNumberObserver(logicIdentifier, 1, vectors, 10.0)
        assertEquals(4.0, obs.absoluteDifference)
        assertEquals(6.0, obs.actual)
        assertEquals(0.4, obs.relativeDifference)

        mutatingVector.scalar = 2.0
        assertEquals(8.0, obs.actual)
    }

    @Test
    fun targetIsZero() {
        val logicIdentifier = "Fake Identifier"
        val mutatingVector = DenseScalableVector(1, 2, 3)
        val vectors = listOf(
            mutatingVector,
        )
        val obs = TargetNumberObserver(logicIdentifier, 1, vectors, 0.0)
        assertEquals(2.0, obs.actual)
        assertEquals(-2.0, obs.delta)
        mutatingVector.scalar = 0.0

        assertEquals(0.0, obs.absoluteDifference)
        assertEquals(1.0, obs.quotientDifference)
    }

    @Test
    fun thouCannotCreateANonsenseObserver() {
        val logicIdentifier = "Fake Identifier"
        val vectors = listOf(DenseScalableVector(1.0, 0.0, 0.0))
        assertThrows<IllegalArgumentException> {
            TargetNumberObserver(logicIdentifier, 4, vectors, 0.0)
        }
        assertThrows<IllegalArgumentException> {
            TargetNumberObserver(logicIdentifier, 1, vectors, 0.0)
        }
        assertThrows<IllegalArgumentException> {
            TargetNumberObserver(logicIdentifier, 2, vectors, 0.0)
        }
        assertDoesNotThrow {
            TargetNumberObserver(logicIdentifier, 0, vectors, 0.0)
        }


    }
}