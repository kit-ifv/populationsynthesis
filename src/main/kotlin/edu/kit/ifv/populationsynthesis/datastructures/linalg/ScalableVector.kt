package edu.kit.ifv.populationsynthesis.datastructures.linalg

import edu.kit.ifv.populationsynthesis.rules.measurement.Measurement

abstract class ScalableVector(
    open var scalar: Double,
    open val baseVector: Vector,
): Vector {

    abstract fun baseNormSquared(): Double
    open fun baseDot(other: Vector): Double {
        return baseVector.dot(other)
    }
    open fun baseDot(other: DoubleArray): Double {
        return baseVector.dot(other)
    }
    override fun euclideanNormSquared(): Double {
        return baseNormSquared() * scalar * scalar
    }
    override fun get(index: Int): Double {
        return scalar * baseVector[index]
    }

    override fun addScaledTo(destination: MutableVector, scalar: Double) {
        return baseVector.addScaledTo(destination, scalar)
    }

    override fun length() = scalar * baseVector.length()
    override fun isEmpty() = size == 0
    override fun iterator(): DoubleIterator {
        return object : DoubleIterator() {
            private val baseIterator = baseVector.iterator()
            private val scalar = this@ScalableVector.scalar
            override fun nextDouble(): Double {
                return baseIterator.nextDouble() * scalar
            }

            override fun hasNext(): Boolean {
                return baseIterator.hasNext()
            }
        }
    }
    companion object {
        fun <T> createFromLogics(element: T, logics: Collection<Measurement<T>>): SparseScalableVector {
            return SparseScalableVector(
                scalar = 1.0,
                SparseVector.fromValues(logics.map { it.measure(element) })
            )
        }
    }

}
