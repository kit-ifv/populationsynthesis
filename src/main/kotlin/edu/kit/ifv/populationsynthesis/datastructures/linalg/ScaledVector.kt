package edu.kit.ifv.populationsynthesis.datastructures.linalg

interface MutableVector: Vector {
    operator fun set(index: Int, value: Double)
}

interface ArrayBackedVector: Vector {
    val backingArray: DoubleArray
}