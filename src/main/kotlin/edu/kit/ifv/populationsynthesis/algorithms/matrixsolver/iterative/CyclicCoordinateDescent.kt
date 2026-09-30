package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.toVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.MutableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.sum
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeSource

class CyclicCoordinateDescent(
    maxIter: Int = 1000,
    maxDuration: Duration = 30.minutes,
) : AbstractIterativeMatrixSolver<ResidualSolverState>(maxIter, maxDuration, SolverStateFactory { a, b ->
    ResidualSolverState(b.toVector(), a.toList())
}) {
    override fun step(
        state: ResidualSolverState,
        iteration: Int
    ) {
        val vectors = state.vectors
        vectors.forEach { vector ->
            val scaling = state.leastSquaresScalar(vector)
            val delta = scaling.coerceAtLeast(-vector.scalar)
            vector.scalar += delta
        }
    }

    override fun finish(state: ResidualSolverState) {
        state.vectors.forEach { vector ->
            vector.originalRef.scalar = vector.scalar
        }
    }
}


class ResidualSolverState(
    override val target: Vector,
    initVectors: List<ScalableVector>
) : MutableSolverState {

    override val vectors: List<ProtectedScalableVector> = initVectors.map {
        ProtectedScalableVector(it)
    }

    override val spawnMark: TimeSource.Monotonic.ValueTimeMark = TimeSource.Monotonic.markNow()

    val residual: MutableVector =
        (target - vectors.sum())

    var maxCoordinateChange: Double = Double.POSITIVE_INFINITY

    fun leastSquaresScalar(vector: ScalableVector): Double {
        val denominator = vector.baseNormSquared()
        return (vector.baseDot(residual) / denominator)
    }

    private fun updateResidual(vector: Vector, delta: Double) {
        vector.forEachNonZeroEntry { index, value ->
            residual[index] -= value * delta
        }
    }

    inner class ProtectedScalableVector(
        val originalRef: ScalableVector
    ) : ScalableVector(originalRef.scalar, originalRef.baseVector) {
        override var scalar: Double
            get() = super.scalar
            set(value) {
                updateResidual(baseVector, delta = value - super.scalar)
                super.scalar = value
            }

        override fun baseNormSquared(): Double {
            return originalRef.baseNormSquared()
        }

        override fun baseDot(other: Vector): Double {
            return originalRef.baseDot(other)
        }

        override fun baseDot(other: DoubleArray): Double {
            return originalRef.baseDot(other)
        }

        override val size: Int get() = baseVector.size

        override fun iterator(): DoubleIterator {
            TODO("Not yet implemented")
        }
    }
}
