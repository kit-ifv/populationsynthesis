package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.toVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.MutableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScaledVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.sum
import kotlin.collections.forEach
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeSource

class CyclicCoordinateDescent(
    maxIter: Int = 1000,
    maxDuration: Duration = 30.minutes,
): AbstractIterativeMatrixSolver<ResidualSolverState>(maxIter, maxDuration, SolverStateFactory { a, b ->
    ResidualSolverState(b.toList(), a.toList())
}) {
    override fun step(
        state: ResidualSolverState,
        iteration: Int
    ) {
        val vectors = state.vectors
        vectors.forEach { vector ->
            val scaling = state.leastSquaresScalar(vector)

        }
    }

    override fun finish(state: ResidualSolverState) {
        TODO("Not yet implemented")
    }
}


class ResidualSolverState(
    override val observers: List<RuleObserver>,
     vectorsvbv: List<ScalableVector>
) : MutableSolverState(observers, vectorsvbv) {

    override val vectors: List<ProtectedScalableVector> = vectorsvbv.map { ProtectedScalableVector(it) }
    override val target: Vector = observers.toVector()
    override val spawnMark: TimeSource.Monotonic.ValueTimeMark = TimeSource.Monotonic.markNow()

    val residual: DoubleArray =
        (target - vectors.sum()).map { it }.toDoubleArray()

    var maxCoordinateChange: Double = Double.POSITIVE_INFINITY

    fun leastSquaresScalar(vector: ScaledVector): Double {
        val denominator = vector.baseNormSquared()
        return (vector.baseDot(residual) / denominator)
    }
    private fun updateResidual(vector: Vector, delta: Double) {
        vector.forEachNonZeroEntry { index, value ->
            residual[index] -= value * delta
        }
    }

    inner class ProtectedScalableVector(
        private val original: ScalableVector
    ): ScaledVector by original, ScalableVector {
        override var scalar: Double
        get() = original.scalar
        set(value) {
            updateResidual(original, original.scalar - value)
            original.scalar = value
        }
        override fun currentValueForIndex(index: Int): Double {
            return original.currentValueForIndex(index)
        }

        override fun attributeForIndex(index: Int): Double {
            return original.attributeForIndex(index)
        }

        override fun appliesToRule(ruleIndex: Int): Boolean {
            return original.appliesToRule(ruleIndex)
        }


    }
}
