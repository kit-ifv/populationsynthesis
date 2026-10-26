package edu.kit.ifv.populationsynthesis.datastructures.linalg

import edu.kit.ifv.populationsynthesis.datastructures.Signature

class SparseScalableVector(
    scalar: Double,
    override val baseVector: SparseVector,
): ScalableVector(scalar, baseVector) {
    constructor(signature: Signature, scalar: Double): this(
        scalar, signature
    )


    constructor(scalar: Double, firstNumber: Number, vararg additionalNumbers: Number): this(
        scalar, SparseVector.fromValues(firstNumber, *additionalNumbers)
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



    override fun toString(): String {
        return "SparseScalingVector(scalar=$scalar, original=$baseVector)"
    }
}