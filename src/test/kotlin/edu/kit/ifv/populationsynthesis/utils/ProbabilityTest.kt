package edu.kit.ifv.populationsynthesis.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class ProbabilityTest {
    @ParameterizedTest
    @ValueSource(doubles = [1.0, 0.5, 0.3, 0.0, 0.1])
    fun probabilityConversion(targets: Double) {

        assertEquals(targets.asProbability().probability, targets, 0.00000001)
    }

    @Test
    fun multiplication() {
        assertEquals(0.5.asProbability() * 0.5.asProbability(), 0.25.asProbability())
    }
}