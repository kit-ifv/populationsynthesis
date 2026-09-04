package edu.kit.ifv.populationsynthesis.datastructures.linalg

class ConstantNormalizedVector(private val original: ConstantVector): NormalizedVector, Vector by original {
    constructor(elements: Collection<Double>): this(ConstantVector(elements))
    override fun length() = 1.0
}