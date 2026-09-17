package edu.kit.ifv.populationsynthesis.datastructures.linalg

import edu.kit.ifv.populationsynthesis.datastructures.Signature
import edu.kit.ifv.populationsynthesis.datastructures.dot
import edu.kit.ifv.populationsynthesis.datastructures.euclidianLength
import edu.kit.ifv.populationsynthesis.datastructures.normalize
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.measurement.Measurement

interface ScalableVector : Vector {
    var scalar: Double
    val signature: Signature
    override operator fun get(index: Int): Double = currentValueForIndex(index)
    fun currentValueForIndex(index: Int): Double
    fun attributeForIndex(index: Int): Double
    fun appliesToRule(ruleIndex: Int): Boolean

    fun attributeLength() = signature.euclidianLength()
    override fun length() = attributeLength() * scalar
    override fun cosineSimilarity(other: Vector): Double {
        if(other is ScalableVector) {
            return signature.dot(other.signature)
        }
        return super.cosineSimilarity(other)
    }

    override fun dot(other: Vector): Double {
        var acc = .0

        signature.forEachEntry { idx, value ->
            acc += other[idx] * value * scalar
        }
        return acc
    }

    fun content() = map { it }

    fun highestIndex(): Int {
        return signature.maxKey + 1
    }
    override fun iterator(): DoubleIterator {
        return object : DoubleIterator() {
            private var index = 0

            override fun hasNext(): Boolean {
                return index <= signature.maxKey
            }

            override fun nextDouble(): Double {
                return currentValueForIndex(index++)
            }
        }
    }
    override val size get() = signature.maxKey + 1

    companion object {
        /**
         * creates a Scalable Vector for a target [element] based on the ruleset defined in [rules]
         */
        fun <T> createFromRules(element: T, rules: Collection<Rule<T>>): ArrayScalableVector {
            return createFromLogics(element, rules.map { it.logic })
        }

        fun <T> createFromLogics(element: T, logics: Collection<Measurement<T>>): ArrayScalableVector {
            return ArrayScalableVector(logics.map { it.measure(element) })
        }

        fun <T> createFrom(element: T, measurements: Collection<Measurement<T>>): ArrayScalableVector {
            return ArrayScalableVector(measurements.map { it.measure(element) })
        }
    }
}

fun ScalableVector.baseVectorDot(other: Vector): Double {
    var acc = .0
    signature.forEachEntry { idx, value ->
        acc += value * other[idx]
    }
    return acc
}


fun ScalableVector.multiplyComponent(other: Vector): ScalableVector {
    val scaled = zip(other) { a, b -> a / scalar * b }
    return SignatureScalableVector(Signature.Companion.fromValues(scaled), scalar)
}

fun ScalableVector.normalize(): NormalizedSignatureVector {
    return NormalizedSignatureVector(signature.normalize(), scalar * signature.euclidianLength())
}



