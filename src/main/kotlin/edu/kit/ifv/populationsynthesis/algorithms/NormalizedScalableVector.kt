package edu.kit.ifv.populationsynthesis.algorithms

import edu.kit.ifv.populationsynthesis.Signature
import edu.kit.ifv.populationsynthesis.dot
import edu.kit.ifv.populationsynthesis.euclidianLength
import edu.kit.ifv.populationsynthesis.normalize
import kotlin.math.abs
import kotlin.math.sign

class NormalizedSignatureVector internal constructor(
    override val signature: Signature,
    override var scalar: Double = 1.0
) : NormalizedVector, ScalableVector {

    override operator fun get(index: Int): Double = currentValueForIndex(index)

    init {
        require(abs(signature.euclidianLength() - 1.0) <= 1e-9) {
            "This vector is not normalized"
        }
    }

    override val size: Int get() = signature.maxKey


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
        return super<NormalizedVector>.dot(other)
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
            denseIndex <= size

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

fun ScalableVector.normalize(): NormalizedSignatureVector {
    return NormalizedSignatureVector(signature.normalize(), scalar * signature.euclidianLength())
}