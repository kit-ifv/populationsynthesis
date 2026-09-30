package edu.kit.ifv.populationsynthesis.datastructures.linalg

import edu.kit.ifv.populationsynthesis.datastructures.Signature
import edu.kit.ifv.populationsynthesis.datastructures.Signatureold
import kotlin.math.abs

class SparseVector private constructor(
    @PublishedApi
    internal var indexArray: IntArray,
    @PublishedApi
    internal var valueArray: DoubleArray,
): Vector {

    constructor(signature: Signature): this(signature.indexArray, signature.valueArray)
    override val size: Int = runCatching { indexArray.last() + 1}.getOrElse { 0 }

    val nonzeroIndices get()= indexArray.iterator()

    override fun get(index: Int): Double {
        for (i in indexArray.indices) if (indexArray[i] == index) return valueArray[i]
        return 0.0
    }
    inline fun forEachEntry(action: (Int, Double) -> Unit) {
        for(i in indexArray.indices) {
            action(indexArray[i], valueArray[i])
        }
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

    override fun manhattanNorm(): Double {
        return valueArray.sumOf{abs(it)}
    }

    fun toScalableVector(initialScalar: Double = 1.0): SparseScalableVector {
        return SparseScalableVector(initialScalar, this)
    }

    override fun forEachNonZeroEntry(action: (Int, Double) -> Unit) {
        indexArray.indices.forEach { action(indexArray[it], valueArray[it]) }
    }
    fun mask(maskArray: BooleanArray): SparseVector {
        val activeIndices = indexArray.withIndex().filter { maskArray[it.value]}
        val activeValues = activeIndices.map {
            valueArray[it.index] // Direct access - no indirection
        }
        return SparseVector(activeIndices.map { it.value }.toIntArray(), activeValues.toDoubleArray())
    }
    override fun toString(): String {
        return indexArray.toList().zip(valueArray.toList()).joinToString {
            "${it.first}=${it.second}"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SparseVector) return false
        return indexArray.contentEquals(other.indexArray) && valueArray.contentEquals(other.valueArray)

    }

    override fun hashCode(): Int {
        return indexArray.contentHashCode() + 31 * valueArray.contentHashCode()
    }

    companion object {
        fun fromSignature(signature: Signatureold): SparseVector {
            return SparseVector(signature.indexArray, signature.valueArray)
        }
        fun fromValues(firstValue: Number, vararg values: Number): SparseVector {
            val values1 = (listOf(firstValue) + values).map { it.toDouble() }
            return fromValues(values1)

        }

        fun fromValues(values: Collection<Double>): SparseVector {
            require(values.isNotEmpty()) {
                "Cannot create empty vectors"
            }

            val indices = mutableListOf<Int>()
            val valuesE = mutableListOf<Double>()
            values.withIndex().forEach {(index, value) ->
                if(value != .0) {
                    indices.add(index)
                    valuesE.add(value)
                }
            }
            return SparseVector(indices.toIntArray(), valuesE.toDoubleArray())
        }

        fun fromMap(map: Map<Int, Double>): SparseVector {
            val values = (0..map.keys.max()).map {
                map[it] ?: .0
            }
            return fromValues(values)

        }
    }
}

fun SparseVector.hasKey(key: Int): Boolean = key in indexArray