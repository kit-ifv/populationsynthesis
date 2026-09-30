package edu.kit.ifv.populationsynthesis.datastructures.linalg

class DenseVector(
    private val backingArray: DoubleArray,
): MutableVector {
    // Force a single element, to disallow construction of a 0 element vector via convenience constructor
    constructor(number: Number, vararg values: Number): this((listOf(number.toDouble()) + values.map { it.toDouble() }).toDoubleArray())
    override fun set(index: Int, value: Double) {
        backingArray[index] = value
    }

    override val size: Int = backingArray.size

    override fun get(index: Int): Double {
        return backingArray.getOrElse(index) {.0}
    }

    override fun iterator(): DoubleIterator {
        return backingArray.iterator()
    }
    operator fun plusAssign(other: DenseVector) {
        backingArray.indices.forEach { i ->
            backingArray[i] += other.backingArray[i]
        }
    }
    operator fun plusAssign(other: Vector) {
        other.forEachNonZeroEntry { idx, value ->
            backingArray[idx] += value
        }
    }
    operator fun minusAssign(other: DenseVector) {
        other.forEachNonZeroEntry { idx, value ->
            backingArray[idx] -= value
        }
    }

    operator fun timesAssign(scalar: Number) {
        val value = scalar.toDouble()
        backingArray.indices.forEach { i ->
            backingArray[i] *= value
        }
    }



}