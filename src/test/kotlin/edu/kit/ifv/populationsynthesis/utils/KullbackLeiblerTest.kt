package edu.kit.ifv.populationsynthesis.utils

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The kullback leibler example taken from english wikipedia: https://en.wikipedia.org/wiki/Kullback%E2%80%93Leibler_divergence
 */
class KullbackLeiblerTest {
    private val p = NumericProbabilityDistribution.fromWeights(9, 12, 4)
    private val q = NumericProbabilityDistribution.fromWeights(1, 1, 1)

    @Test
    fun calculatePQ() {
        assertEquals(p.kullbackLeiblerDivergence(q), 0.0852996, 0.0000001)
    }

    @Test
    fun calculateQP() {
        assertEquals(q.kullbackLeiblerDivergence(p), 0.097455, 0.000001)
    }

    @Test
    fun calculateIdentity() {
        assertEquals(p.kullbackLeiblerDivergence(p), 0.0)
        assertEquals(q.kullbackLeiblerDivergence(q), 0.0)
    }
}

