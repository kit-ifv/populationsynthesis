package edu.kit.ifv.populationsynthesis.algorithms

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import edu.kit.ifv.populationsynthesis.algorithms.ipu.nnlsSpark
import org.ejml.data.DMatrixRMaj
import java.util.Arrays.stream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class LSQRTest {
    @Test
    fun testExactSolution() {
        val A = DMatrixRMaj(
            2, 2, true,
            1.0, 0.0,
            0.0, 1.0
        )

        val b = doubleArrayOf(3.0, -2.0)

        val result = lsqr(A, b)
        assertEquals(3.0, result.x[0], 1e-8)
        assertEquals(-2.0, result.x[1], 1e-8)
        assertTrue(result.r1norm < 1e-8)
    }

    @Test
    fun testLeastSquaresConsistent() {
        val A = DMatrixRMaj(
            3, 2, true,
            1.0, 0.0,
            1.0, 1.0,
            0.0, 1.0
        )

        val b = doubleArrayOf(1.0, 0.0, -1.0)

        val result = lsqr(A, b)

        assertEquals(1.0, result.x[0], 1e-6)
        assertEquals(-1.0, result.x[1], 1e-6)
        assertTrue(result.r1norm < 1e-6)
    }

//    @Test
//    fun currentExampleRun() {
//        val A = loadMatrixFromCsv("/exampleMatrix.csv")
//        assertEquals(A.get(6, 0), 1.0)
//        val b = doubleArrayOf(282.0, 358.0, 458.0, 656.0, 390.0, 763.0, 1974.0, 3552.0, 1448.0, 1372.0, 1589.0, 2590.0, 2329.0, 753.0, 547.0, 294.0)
//
//        val nnls = nnlsSpark(A, b)
//        val result = lsqr(A, b).x
//        assertEquals(result[0], 8.019781663, 0.000001)
//    }

    private
    fun loadMatrixFromCsv(resourcePath: String): DMatrixRMaj {
        val inputStream = object {}.javaClass.getResourceAsStream(resourcePath)
            ?: error("Resource not found: $resourcePath")

        val lines = inputStream.bufferedReader().readLines()
            .filter { it.isNotBlank() }

        val rows = lines.map { line ->
            line.split(',').map { it.trim().toDouble() }
        }

        val numRows = rows.size
        val numCols = rows.first().size
        require(rows.all { it.size == numCols }) { "Inconsistent column count" }

        val data = DoubleArray(numRows * numCols)
        for (r in 0 until numRows) {
            for (c in 0 until numCols) {
                data[r * numCols + c] = rows[r][c]
            }
        }

        return DMatrixRMaj(numRows, numCols, true, *data)
    }
}