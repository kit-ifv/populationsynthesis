package edu.kit.ifv.populationsynthesis.datastructures.linalg

class DenseVector(
    override val backingArray: DoubleArray,
): MutableVector, ArrayBackedVector {
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

    override fun addScaledTo(
        destination: MutableVector,
        scalar: Double
    ) {
        backingArray.indices.forEach { i ->
            destination[i] += scalar * backingArray[i]
        }
    }

    operator fun minusAssign(other: Vector) {
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



    override fun toString(): String {
        return "V(${indices.map { get(it) }.joinToString()})"
    }


}