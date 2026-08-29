package edu.kit.ifv.populationsynthesis.datastructures

import org.ejml.data.DMatrixRMaj
import org.ejml.dense.row.CommonOps_DDRM
import kotlin.test.Test

class MatrixTest {
    @Test
    fun testCalculations() {


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
}