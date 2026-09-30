package edu.kit.ifv.populationsynthesis.datastructures.linalg

import edu.kit.ifv.populationsynthesis.datastructures.Signature
import edu.kit.ifv.populationsynthesis.datastructures.dot
import edu.kit.ifv.populationsynthesis.datastructures.euclidianLength
import kotlin.math.abs
import kotlin.math.sign

class NormalizedSignatureVector constructor(
    override val signature: Signature,
    override var scalar: Double = 1.0
) : NormalizedVector, ScalableVector {

    override operator fun get(index: Int): Double = currentValueForIndex(index)

    init {
        require(abs(signature.euclidianLength() - 1.0) <= 1e-9) {
            "This vector is not normalized"
        }
    }



    override fun currentValueForIndex(index: Int): Double {
        return scalar * attributeForIndex(index)
    }

    override fun attributeForIndex(index: Int): Double {
        return signature[index]
    }

    override fun appliesToRule(ruleIndex: Int): Boolean {
        return attributeForIndex(ruleIndex) != .0
    }


    override fun length(): Double {
        return scalar
    }

    val sign get() = scalar.sign

    override fun dot(other: Vector): Double {
        if (other is NormalizedSignatureVector) {
            return signature.dot(other.signature)
        }
        return super<ScalableVector>.dot(other)
    }

    override fun cosineSimilarity(other: Vector): Double {
        if(other is NormalizedSignatureVector) {
            return dot(other) * sign * other.sign
        }
        return super<ScalableVector>.cosineSimilarity(other)
    }


    override fun iterator(): DoubleIterator {
        return SparseDoubleIterator()
    }



    private inner class SparseDoubleIterator(
    ) : DoubleIterator() {

        private var denseIndex = 0
        private var sparseIndex = 0

        override fun hasNext(): Boolean =
            denseIndex < size

        override fun nextDouble(): Double {
            if (!hasNext()) throw NoSuchElementException()

            val value =
                if (
                    sparseIndex < signature.indexArray.size &&
                    signature.indexArray[sparseIndex] == denseIndex
                ) {
                    signature.valueArray[sparseIndex++] * scalar
                } else {
                    0.0
                }

            denseIndex++
            return value
        }
    }
}