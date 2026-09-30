package edu.kit.ifv.populationsynthesis.datastructures.linalg

import edu.kit.ifv.populationsynthesis.rules.measurement.Measurement

abstract class ScalableVector(
    open var scalar: Double,
    open val baseVector: Vector,
): Vector {

    abstract fun baseNormSquared(): Double
    abstract fun baseDot(other: Vector): Double
    abstract fun baseDot(other: DoubleArray): Double
    override fun euclideanNormSquared(): Double {
        return baseNormSquared() * scalar * scalar
    }
    override fun get(index: Int): Double {
        return scalar * baseVector[index]
    }
    override fun length() = scalar * baseVector.length()
    override fun isEmpty() = size == 0

    companion object {
        fun <T> createFromLogics(element: T, logics: Collection<Measurement<T>>): SparseScalableVector {
            return SparseScalableVector(
                scalar = 1.0,
                SparseVector.fromValues(logics.map { it.measure(element) })
            )
        }
    }

}

interface MutableVector: Vector {
    operator fun set(index: Int, value: Double)
}