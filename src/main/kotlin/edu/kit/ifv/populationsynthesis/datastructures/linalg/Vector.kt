package edu.kit.ifv.populationsynthesis.datastructures.linalg

import java.lang.Double.max
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

interface Vector {

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

    fun euclideanNormSquared(): Double {
        var acc = .0
        iterator().forEach {
            acc += it.pow(2)
        }
        return acc
    }
    fun euclideanLength() = euclideanNormSquared() / length()
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
    operator fun times(scalar: Double): Vector {
        val backingArray = DoubleArray(size)
        for(i in indices) backingArray[i] = scalar * get(i)
        return DenseVector(backingArray)
    }


    fun cosineSimilarity(other: Vector): Double {
        return this.dot(other) / (this.length() * other.length())
    }

    fun safeCosineSimilarity(other: Vector): Double {
        val cosine = cosineSimilarity(other)
        return if (cosine.isNaN()) 0.0 else cosine
    }

    operator fun plus(other: Vector): MutableVector {
        val result = DoubleArray(maxOf(size, other.size))

        for (i in 0 until size) {
            result[i] += this[i]
        }

        for (i in 0 until other.size) {
            result[i] += other[i]
        }

        return DenseVector(result)
    }

    fun forEachNonZeroEntry(action: (idx: Int, value: Double) -> Unit) {
        iterator().withIndex().forEach {
            action(it.index, it.value)
        }
    }

    operator fun minus(other: Vector): MutableVector {
        val result = DoubleArray(maxOf(size, other.size))

        for (i in 0 until size) {
            result[i] += this[i]
        }

        for (i in 0 until other.size) {
            result[i] -= other[i]
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
fun Vector.normalize(): NormalizedVector {
    val length = this.length()
    return ConstantNormalizedVector(indices.map { get(it) / length })
}
/**
 * Guarantees that the entries of the vector are equal to 1 under euclidian norm, making it significantly easier to calculate
 * certain operations
 */
interface NormalizedVector: Vector


fun Collection<Number>.asVector(): ConstantVector {
    return ConstantVector(map { it.toDouble() })
}

fun Collection<Vector>.strictlyPositiveSum(): Vector {
    if (isEmpty()) {
        return ConstantVector(DoubleArray(0))
    }

    val maxSize = maxOf { it.size }
    val result = DoubleArray(maxSize)

    for (vector in this) {
        for (i in 0 until vector.size) {
            result[i] += max(.0, vector[i])
        }
    }

    return ConstantVector(result)
}

fun Collection<Vector>.sum(): Vector {
    if (isEmpty()) {
        return ConstantVector(DoubleArray(0))
    }

    val maxSize = maxOf { it.size }
    val result = DoubleArray(maxSize)

    for (vector in this) {
        for (i in 0 until vector.size) {
            result[i] += vector[i]
        }
    }

    return ConstantVector(result)
}

fun Vector.isRelevantFor(targetIndices: Collection<Int>) = targetIndices.any {get(it) != .0}
fun Vector.toList() = indices.map { get(it) }