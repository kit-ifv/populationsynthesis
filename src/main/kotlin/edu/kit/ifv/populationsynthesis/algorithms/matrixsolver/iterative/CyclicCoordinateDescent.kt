package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.datastructures.linalg.DenseVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.MutableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.SparseVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.sum
import kotlin.time.TimeSource


interface ResidualState : SolverState {
    override val vectors: List<ResidualSolverState.ProtectedScalableVector>
    val residual: Vector
    fun leastSquaresScalar(vector: ResidualSolverState.ProtectedScalableVector): Double {
        val denominator = vector.baseNormSquared()
        return (vector.baseDot(residual) / denominator)
    }
}


interface MutableResidualState : ResidualState, MutableSolverState {
    companion object {

    }
}

class ResidualSolverState constructor(
    override val target: Vector,
    initVectors: List<ScalableVector>
) : MutableResidualState {

    override val vectors: List<ProtectedScalableVector> = initVectors.map {
        ProtectedScalableVector(it, it.baseVector as SparseVector)
    }

    override val spawnMark: TimeSource.Monotonic.ValueTimeMark = TimeSource.Monotonic.markNow()


    override val residual: Vector
        field: DenseVector = (target - vectors.sum())

    var maxCoordinateChange: Double = Double.POSITIVE_INFINITY

//    override fun leastSquaresScalar(vector: ScalableVector): Double {
//        val denominator = vector.baseNormSquared()
//        return (vector.baseDot(residual) / denominator)
//    }

    private fun updateResidual(vector: Vector, delta: Double) {
        vector.addScaledTo(residual, -delta)
//        vector.forEachNonZeroEntry { index, value ->
//            residual[index] =
//                residual[index] - value * delta
//        }
    }

    override fun finish() {
        vectors.forEach { it.originalRef.scalar = it.scalar }
    }

    override fun leastSquaresScalar(vector: ProtectedScalableVector): Double {
        val other = residual.backingArray
        val baseDot = vector.baseDot(other)
        val inverseBaseNormSquared = vector.inverseBaseNormSquared
        return baseDot * inverseBaseNormSquared
    }



    inner class ProtectedScalableVector constructor(
        val originalRef: ScalableVector,
        override val baseVector: SparseVector,
    ) : ScalableVector(originalRef.scalar, originalRef.baseVector) {
        override var scalar: Double
            get() = super.scalar
            set(value) {
                updateResidual(baseVector, delta = value - super.scalar)
                super.scalar = value
            }
        val inverseBaseNormSquared =
            1.0 / baseVector.euclideanNormSquared()
        val baseNormSquaredField = originalRef.baseVector.euclideanNormSquared()
        override fun baseNormSquared(): Double {
            return baseNormSquaredField
        }

        override fun baseDot(other: Vector): Double {
            return baseVector.dot(other)
        }

        override fun baseDot(other: DoubleArray): Double {
            return baseVector.dot(other)
        }

        override val size: Int get() = baseVector.size

        override fun iterator(): DoubleIterator {
            return object : DoubleIterator() {
                private val baseIterator = baseVector.iterator()
                private val scalar = this@ProtectedScalableVector.scalar
                override fun nextDouble(): Double {
                    return baseIterator.nextDouble() * scalar
                }

                override fun hasNext(): Boolean {
                    return baseIterator.hasNext()
                }
            }
        }

        override fun toString(): String {
            val accumulator = StringBuilder()
            iterator().forEach {
                accumulator.append(scalar * it)
                accumulator.append(", ")
            }
            return "ProtectedScalableVector(=$accumulator)"
        }
    }
}
