package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver

import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import kotlin.math.abs
import kotlin.math.max

class TargetNumberObserver constructor(
    identifier: String,
    observedIndex: Int,
    vectors: List<ScalableVector>,
    override val expected: Double,

    ) : RuleObserver(
    identifier,
    observedIndex,
    vectors,
) {
    override val absoluteDifference: Double get() = abs(expected - sum())
    override val relativeDifference: Double get() = absoluteDifference / expected

    @Suppress("MagicNumber")
    override val quotientDifference: Double
        get() {
            val fallback = 1e-9
            val exp = if (expected == 0.0) fallback else expected
            val act = if (sum() == 0.0) fallback else sum()
            return max(exp / act, act / exp)
        }

    override fun optimize(): Double {
        val sum = sum()
//        if(sum == 0.0) return // There is no remaining vector with a scalar > 0.0. This observer can no longer be optimized.
        val currentSum = if (sum == 0.0) {
            if(expected == 0.0) {
                return 1.0
            }
            fallbackSize()
        } else sum

        // TODO fallback calculation if expected != 0.0 and sum is 0.0
        val scalingFactor: Double = expected / currentSum
        this.timesAssign(scalingFactor)
        return scalingFactor
    }
}