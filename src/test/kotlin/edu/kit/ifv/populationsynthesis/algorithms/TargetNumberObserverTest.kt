package edu.kit.ifv.populationsynthesis.algorithms

import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class TargetNumberObserverTest {

    @Test
    fun properBehaviour(){
        val logicIdentifier = "Fake Identifier"
        val mutatingVector = ScalableVector(1, 2, 3)
        val vectors = listOf(
            ScalableVector(1, 2, 3),
            mutatingVector,
            ScalableVector(1, 2, 3),
        )
        val obs = TargetNumberObserver(logicIdentifier, 1, vectors, 10.0)
        assertEquals(obs.absoluteDifference, 4.0)
        assertEquals(obs.actual, 6.0)
        assertEquals(obs.relativeDifference, 0.4)

        mutatingVector.scalar = 2.0
        assertEquals(obs.actual, 8.0)
    }
    @Test
    fun targetIsZero(){
        val logicIdentifier = "Fake Identifier"
        val mutatingVector = ScalableVector(1, 2, 3)
        val vectors = listOf(
            mutatingVector,
        )
        val obs = TargetNumberObserver(logicIdentifier, 1, vectors, 0.0)
        assertEquals(obs.actual,2.0)
        assertEquals(obs.delta, -2.0)
        mutatingVector.scalar = 0.0

        assertEquals(obs.absoluteDifference, 0.0)
        assertEquals(obs.quotientDifference, 1.0)
    }

    @Test
    fun thouCannotCreateANonsenseObserver() {
        val logicIdentifier = "Fake Identifier"
        val vectors = listOf(ScalableVector(1.0, 0.0, 0.0))
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
            TargetNumberObserver(logicIdentifier, 0 , vectors, 0.0)
        }


    }
}