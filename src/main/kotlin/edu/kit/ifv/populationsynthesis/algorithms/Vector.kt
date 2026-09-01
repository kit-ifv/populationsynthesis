package edu.kit.ifv.populationsynthesis.algorithms

import edu.kit.ifv.populationsynthesis.Signature
import kotlin.math.pow
import kotlin.math.sqrt

interface Vector : Iterable<Double> {
    val size: Int
    operator fun get(index: Int): Double
    override fun iterator(): DoubleIterator // We know that we operate on doubles only.
    fun dot(other: Vector): Double {
        return zip(other).sumOf { (a, b) -> a * b }
    }

    fun length(): Double {
        return sqrt(this.sumOf { it.pow(2) })
    }

    operator fun times(scalar: Double): Vector {
        return ConstantVector(map { it * scalar })
    }


    fun cosineSimilarity(other: Vector): Double {
        return this.dot(other) / (this.length() * other.length())
    }
    fun safeCosineSimilarity(other: Vector): Double {
        val cosine = cosineSimilarity(other)
        return if(cosine.isNaN()) 0.0 else cosine
    }
    operator fun plus(other: Vector): ConstantVector {
        val result = DoubleArray(maxOf(size, other.size))

        for (i in 0 until size) {
            result[i] += this[i]
        }

        for (i in 0 until other.size) {
            result[i] += other[i]
        }

        return ConstantVector(result)
    }

    operator fun minus(other: Vector): ConstantVector {
        val result = DoubleArray(maxOf(size, other.size))

        for (i in 0 until size) {
            result[i] += this[i]
        }

        for (i in 0 until other.size) {
            result[i] -= other[i]
        }

        return ConstantVector(result)
    }

    fun contentEquals(other: Vector): Boolean {
        return zip(other).all { (a, b) -> a == b }
    }

    companion object {
        operator fun invoke(vararg constants: Double): ConstantVector {
            return ConstantVector(constants)
        }
    }
}
fun Vector.safeComponentInverse(replaceZerosWith: Double = 1.0): Vector = ConstantVector(map {
    if (it == .0) replaceZerosWith else 1.0 / it
})
fun Vector.toSignature() = Signature.fromMap(map { it }.withIndex().associate { (i, v) -> i to v })
fun Vector.toSignatureVector(): ScalableVector = SignatureScalableVector(toSignature(), 1.0)
fun Vector.normalize(): NormalizedVector {
    val length = this.length()
    return ConstantNormalizedVector(map { it / length })
}
/**
 * Guarantees that the entries of the vector are equal to 1 under euclidian norm, making it significantly easier to calculate
 * certain operations
 */
interface NormalizedVector: Vector


fun Collection<Number>.asVector(): ConstantVector {
    return ConstantVector(map { it.toDouble() })
}

fun Collection<Vector>.sum(): Vector {
    if (isEmpty()) {
        return ConstantVector(DoubleArray(0))
    }

    val maxSize = maxOf { it.size }
    val result = DoubleArray(maxSize + 1)

    for (vector in this) {
        for (i in 0 .. vector.size) {
            result[i] += vector[i]
        }
    }

    return ConstantVector(result)
}

class ConstantNormalizedVector(private val original: ConstantVector): NormalizedVector, Vector by original {
    constructor(elements: Collection<Double>): this(ConstantVector(elements))
    override fun length() = 1.0
}

class ConstantVector(private val content: DoubleArray) : Vector {
    override val size = content.size

    constructor(elements: Collection<Double>) : this(elements.toDoubleArray())

    override fun iterator(): DoubleIterator {
        return content.iterator()
    }

    override fun get(index: Int): Double {
        return runCatching { content[index] }.getOrNull() ?: 0.0
    }

    override fun toString(): String {
        return content.contentToString()
    }
    companion object {
        fun uniform(value: Double = 1.0, size: Int): ConstantVector {
            return ConstantVector(DoubleArray(size) {value})
        }
    }
}