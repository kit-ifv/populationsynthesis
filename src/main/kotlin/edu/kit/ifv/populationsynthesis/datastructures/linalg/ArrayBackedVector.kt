package edu.kit.ifv.populationsynthesis.datastructures.linalg

interface ArrayBackedVector: Vector {
    val backingArray: DoubleArray
}