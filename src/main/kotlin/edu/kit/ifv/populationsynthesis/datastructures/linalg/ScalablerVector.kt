package edu.kit.ifv.populationsynthesis.datastructures.linalg

class SparseScalingVector(
    scalar: Double,
    original: SparseVector,
): ScaledVector(scalar, original) {

    val baseNormSquared = original.euclideanNormSquared()
    override fun baseNormSquared(): Double {
        return baseNormSquared
    }

    override fun baseDot(other: Vector): Double {
        return original.dot(other)
    }

    override fun baseDot(other: DoubleArray): Double {
        return original.dot(other)
    }

    override val size: Int = original.size

    override fun get(index: Int): Double {
        return scalar * original[index]
    }

    override fun iterator(): DoubleIterator {
        TODO()
    }

    override fun toString(): String {
        return "SparseScalingVector(scalar=$scalar, original=$original)"
    }
}