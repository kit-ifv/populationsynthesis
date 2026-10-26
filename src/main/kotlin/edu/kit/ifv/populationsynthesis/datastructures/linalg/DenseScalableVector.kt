package edu.kit.ifv.populationsynthesis.datastructures.linalg

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