package edu.kit.ifv.populationsynthesis.datastructures.linalg

import edu.kit.ifv.populationsynthesis.datastructures.Signature
import edu.kit.ifv.populationsynthesis.datastructures.toSparseVector

class SparseScalableVector(
    scalar: Double,
    override val baseVector: SparseVector,
): ScalableVector(scalar, baseVector) {
    constructor(signature: Signature, scalar: Double): this(
        scalar, signature.toSparseVector()
    )
    val baseNormSquared = baseVector.euclideanNormSquared()
    override fun baseNormSquared(): Double {
        return baseNormSquared
    }

    override fun baseDot(other: Vector): Double {
        return baseVector.dot(other)
    }

    override fun baseDot(other: DoubleArray): Double {
        return baseVector.dot(other)
    }

    override val size: Int = baseVector.size

    override fun get(index: Int): Double {
        return scalar * baseVector[index]
    }

    override fun iterator(): DoubleIterator {
        TODO()
    }

    override fun toString(): String {
        return "SparseScalingVector(scalar=$scalar, original=$baseVector)"
    }
}
class DenseScalableVector(
    scalar: Double,
    original: DenseVector,
): ScalableVector(scalar, original) {

    constructor(vararg elements: Number): this(1.0, DenseVector(elements.map { it.toDouble() }.toDoubleArray()))
    override fun baseNormSquared(): Double {
        TODO("Not yet implemented")
    }

    override fun baseDot(other: Vector): Double {
        TODO("Not yet implemented")
    }

    override fun baseDot(other: DoubleArray): Double {
        TODO("Not yet implemented")
    }

    override val size: Int get() = baseVector.size

    override fun iterator(): DoubleIterator {
        TODO("Not yet implemented")
    }
}