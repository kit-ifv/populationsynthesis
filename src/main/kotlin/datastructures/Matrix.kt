package edu.kit.ifv.populationsynthesis.datastructures

import org.ejml.data.DMatrixRMaj
import org.ejml.dense.row.CommonOps_DDRM

class Matrix private constructor(
    val context: DoubleArray,
    private val cols: Int
) {
    val rows: Int get() = context.size / cols
    operator fun times(vector: Vector): Vector {
        require(vector.size == cols) {
            "Dimension mismatch: Matrix is ${rows}x$cols but vector has size ${vector.size}"
        }

        val result = DoubleArray(rows)

        for (r in 0 until rows) {
            var sum = 0.0
            val rowOffset = r * cols
            for (c in 0 until cols) {
                sum += context[rowOffset + c] * vector.content[c]
            }
            result[r] = sum
        }

        return Vector(result)
    }

    operator fun get(row: Int): DoubleArray {
        return context.sliceArray(cols * row until cols * (row + 1))

    }

    operator fun get(row: Int, column: Int): Double {
        require(row in 0 until rows)
        require(column in 0 until cols)
        return context[row * cols + column]
    }

    companion object {
        fun instantiate(elements: Collection<Collection<Double>>): Matrix {
            val expectedSize = elements.first().size
            require(elements.all { it.size == expectedSize }) {" This not good"}

            return Matrix(elements.flatten().toDoubleArray(), expectedSize)
        }
    }
}


fun main() {

    val A = DMatrixRMaj(
        arrayOf(
            doubleArrayOf(1.0, 2.0),
            doubleArrayOf(3.0, 4.0)
        )
    )

    val x = DMatrixRMaj(doubleArrayOf(5.0, 6.0))

    val y = DMatrixRMaj(A.numRows, 1)
    CommonOps_DDRM.mult(A, x, y)
    println("Done")
}