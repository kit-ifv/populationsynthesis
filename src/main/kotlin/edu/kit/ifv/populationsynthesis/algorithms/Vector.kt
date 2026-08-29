package edu.kit.ifv.populationsynthesis.algorithms

import kotlin.math.pow
import kotlin.math.sqrt

interface Vector: Iterable<Double> {


    override fun iterator(): DoubleIterator // We know that we operate on doubles only.
    operator fun times(other: Vector): Double {
        return zip(other).sumOf { (a, b) -> a * b }
    }

    fun length(): Double {
        return sqrt(this.sumOf { it.pow(2) })
    }


    fun cosineSimilarity(other: Vector): Double {
        return this * other / (this.length() * other.length())
    }


}