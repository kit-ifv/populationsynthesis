package edu.kit.ifv.populationsynthesis.datastructures.linalg

class ConstantVector(private val content: DoubleArray) : Vector {
    override val size = content.size
    private val length = super.length()
    override fun length() = length
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

