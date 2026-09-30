package edu.kit.ifv.populationsynthesis.datastructures.linalg

import edu.kit.ifv.populationsynthesis.datastructures.Signature

abstract class ScaledVector(
    open var scalar: Double,
    val original: Vector,
): Vector {

    abstract fun baseNormSquared(): Double
    abstract fun baseDot(other: Vector): Double
    abstract fun baseDot(other: DoubleArray): Double
    override fun euclideanNormSquared(): Double {
        return baseNormSquared() * scalar * scalar
    }
    override fun get(index: Int): Double {
        return scalar * original[index]
    }
    override fun length() = scalar * original.length()


}

interface MutableVector: Vector {
    operator fun set(index: Int, value: Double)
}