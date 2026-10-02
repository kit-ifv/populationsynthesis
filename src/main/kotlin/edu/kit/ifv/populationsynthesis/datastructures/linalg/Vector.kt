package edu.kit.ifv.populationsynthesis.datastructures.linalg

import java.lang.Double.max
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

interface Vector {
    /**
     * The last dimension on this vector that has a nonzero element plus one, e
     */
    val size: Int
    val indices get() = 0 until size
    operator fun get(index: Int): Double
    fun iterator(): DoubleIterator // We know that we operate on doubles only.
    fun dot(other: Vector): Double {
        return indices.sumOf {
            this[it] * other[it]
        }
    }
    fun dot(values: DoubleArray): Double {
        return indices.sumOf {
            this[it] * values[it]
        }
    }
    fun length(): Double {
        return sqrt(euclideanNormSquared())
    }

    fun addScaledTo(
        destination: MutableVector,
        scalar: Double,
    )

    fun euclideanNormSquared(): Double {
        var acc = .0
        iterator().forEach {
            acc += it.pow(2)
        }
        return acc
    }
    // Just a t
    fun euclideanLength() = length()
    /**
     * L1 on a vector
     */
    fun manhattanNorm(): Double {
        var acc = .0
        iterator().forEach {
            acc += abs(it)
        }
        return acc
    }
    fun chebyshevNorm(): Double {
        var acc = .0
        iterator().forEach {
            acc = max(acc, it)
        }
        return acc
    }
    fun cosineSimilarity(other: Vector): Double {
        return this.dot(other) / (this.length() * other.length())
    }

    fun safeCosineSimilarity(other: Vector): Double {
        val cosine = cosineSimilarity(other)
        return if (cosine.isNaN()) 0.0 else cosine
    }



    fun forEachNonZeroEntry(action: (idx: Int, value: Double) -> Unit) {
        iterator().withIndex().forEach {
            action(it.index, it.value)
        }
    }
    /*
    Operators
     */
    operator fun plus(other: Vector): DenseVector {
        val result = DoubleArray(maxOf(size, other.size))

        for (i in indices) {
            result[i] += this[i]
        }

        for (i in other.indices) {
            result[i] += other[i]
        }

        return DenseVector(result)
    }
    operator fun minus(other: Vector): DenseVector {
        val result = DoubleArray(maxOf(size, other.size))

        for (i in indices) {
            result[i] += this[i]
        }

        for (i in other.indices) {
            result[i] -= other[i]
        }

        return DenseVector(result)
    }
    operator fun unaryMinus(): MutableVector {
        val result = DoubleArray(size)
        for (i in indices) {
            result[i] = -this[i]
        }
        return DenseVector(result)
    }


    operator fun times(scalar: Double): MutableVector {
        val result = DoubleArray(size)
        for (i in indices) {
            result[i] = this[i] * scalar

        }
        return DenseVector(result)
    }
    operator fun div(scalar: Double): MutableVector {
        if(scalar == .0) throw IllegalArgumentException("Division by zero cannot create a vector")
        val result = DoubleArray(size)
        for (i in indices) {
            result[i] = this[i] / scalar

        }
        return DenseVector(result)
    }


    fun isEmpty() = size == 0

    fun contentEquals(other: Vector): Boolean {
        return indices.all {
            this[it] == other[it]
        }
    }

    fun contentEquals(other: DoubleArray): Boolean {
        return indices.all {
            this[it] == other[it]
        }
    }
    fun contentEquals(other: List<Double>): Boolean {
        return indices.all {
            this[it] == other[it]
        }
    }

    fun contentEquals(vararg other: Number): Boolean = contentEquals(other.map { it.toDouble() })


    companion object {
        operator fun invoke(firstConstant: Number, vararg constants: Number): Vector {
            return DenseVector(firstConstant, *constants)
        }
    }
}


fun Vector.safeComponentInverse(replaceZerosWith: Double = 1.0): Vector = DenseVector(indices.map {
    val value = get(it)
    if (value == .0) replaceZerosWith else 1.0 / value
}.toDoubleArray())
fun Vector.toSparseVector() = SparseVector.fromValues(indices.map {get(it)})
fun Vector.toSparseScalableVector(): SparseScalableVector =
    SparseScalableVector(toSparseVector(), 1.0)

fun Collection<Vector>.sum(): MutableVector {
    val maxSize = maxOf { it.size }
    val backingArray = DoubleArray(maxSize)
    val target = DenseVector(backingArray)
    forEach {
        target += it
    }
    return target
}

fun Collection<Vector>.strictlyPositiveSum(): MutableVector {
    val maxSize = maxOf { it.size }
    val backingArray = DoubleArray(maxSize)
    val target = DenseVector(backingArray)
    forEach { vector ->
        (0 until maxSize).forEach { index ->
            backingArray[index] += max(vector[index], .0)

        }
    }
    return target
}

fun Vector.isRelevantFor(targetIndices: Collection<Int>) = targetIndices.any {get(it) != .0}
fun Vector.toList() = indices.map { get(it) }