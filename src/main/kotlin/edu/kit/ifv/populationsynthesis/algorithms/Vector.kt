package edu.kit.ifv.populationsynthesis.algorithms

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
fun Collection<Number>.asVector(): ConstantVector {
    return ConstantVector(map { it.toDouble() })
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

class ConstantVector(private val content: DoubleArray) : Vector {
    override val size = content.size

    constructor(elements: Collection<Double>) : this(elements.toDoubleArray())

    override fun iterator(): DoubleIterator {
        return content.iterator()
    }

    override fun get(index: Int): Double {
        return runCatching { content[index] }.getOrNull() ?: 0.0
    }

}