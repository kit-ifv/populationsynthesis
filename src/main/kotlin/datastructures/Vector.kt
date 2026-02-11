package edu.kit.ifv.populationsynthesis.datastructures

import kotlin.math.sqrt

class Vector(
    val content: DoubleArray
) {
    val size get() = content.size

    fun norm(): Double {
        var sum = 0.0
        for (v in content) {
            sum += v * v
        }
        return sqrt(sum)
    }

    operator fun plus(other: Vector): Vector {
        require(size == other.size)
        val result = DoubleArray(size)
        for (i in 0 until size) {
            result[i] = content[i] + other.content[i]
        }
        return Vector(result)
    }

    operator fun minus(other: Vector): Vector {
        require(size == other.size)
        val result = DoubleArray(size)
        for (i in 0 until size) {
            result[i] = content[i] - other.content[i]
        }
        return Vector(result)
    }

    operator fun times(scalar: Double): Vector {
        val result = DoubleArray(size)
        for (i in 0 until size) {
            result[i] = content[i] * scalar
        }
        return Vector(result)
    }

    infix fun dot(other: Vector): Double {
        require(size == other.size)
        var sum = 0.0
        for (i in 0 until size) {
            sum += content[i] * other.content[i]
        }
        return sum
    }
}