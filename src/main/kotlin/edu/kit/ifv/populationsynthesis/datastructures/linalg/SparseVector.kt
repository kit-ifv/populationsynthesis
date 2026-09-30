package edu.kit.ifv.populationsynthesis.datastructures.linalg

import edu.kit.ifv.populationsynthesis.datastructures.Signature

class SparseVector private constructor(
    private var indexArray: IntArray,
    private var valueArray: DoubleArray,
): Vector {


    override val size: Int = indexArray.last() + 1

    override fun get(index: Int): Double {
        for (i in indexArray.indices) if (indexArray[i] == index) return valueArray[i]
        return 0.0
    }

    override fun dot(other: Vector): Double {
        return indexArray.sumOf { valueArray[it] * other[it] }
    }

    override fun dot(values: DoubleArray): Double {
        return indexArray.sumOf { valueArray[it] * values[it] }
    }
    override fun euclideanNormSquared(): Double {
        return valueArray.sumOf { it * it }
    }
    override fun iterator(): DoubleIterator {
        TODO("Not yet implemented")
    }

    override fun forEachNonZeroEntry(action: (Int, Double) -> Unit) {
        indexArray.indices.forEach { action(indexArray[it], valueArray[it]) }
    }

    override fun toString(): String {
        return indexArray.toList().zip(valueArray.toList()).joinToString {
            "${it.first},${it.second}"
        }
    }
    companion object {
        fun fromSignature(signature: Signature): SparseVector {
            return SparseVector(signature.indexArray, signature.valueArray)
        }
        fun fromValues(firstValue: Number, vararg values: Number): SparseVector {
            val numbers = listOf(firstValue) + values
            return fromSignature(Signature.fromValues(numbers.map { it.toDouble() }))
        }
    }
}