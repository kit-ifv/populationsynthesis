package edu.kit.ifv.populationsynthesis.algorithms.ipu

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import org.apache.spark.mllib.optimization.NNLS
import org.ejml.data.DMatrixRMaj
import org.ejml.data.DMatrixSparseCSC
import org.ejml.dense.row.CommonOps_DDRM
import org.ejml.ops.DConvertMatrixStruct
import org.ejml.sparse.csc.CommonOps_DSCC

object SparkNonnegativeLeastSquares : GenericIPU {
    override fun run(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ) {
        val mtx = vectors.toMatrix()



        val b = observers.map { it.expected }.toDoubleArray()
        val solution = nnlsSparkOld(
            mtx,
            b,
//            vectors.map { it.scalar * 4315 }.toDoubleArray()
        )
        vectors.zip(solution.toList()).forEach { (vector, calculatedScalar) ->
            vector.scalar = calculatedScalar
        }
    }

}


fun nnlsSpark(A: DMatrixRMaj, b: DoubleArray, startingValues : DoubleArray? = null): DoubleArray {
    val m = A.numRows
    val n = A.numCols
    require(b.size == m)
    val bMat = DMatrixRMaj(m, 1, true, *b)

    val residual = DMatrixRMaj(A.numRows, 1)  // re
    val initialArray = startingValues ?: DoubleArray(n) { 1.0 }
    val x = DMatrixRMaj(n, 1, true, *initialArray)
    val g = DMatrixRMaj(A.numCols, 1)
    repeat(10000) {
        CommonOps_DDRM.mult(A, x, residual)       // r = A * x
        CommonOps_DDRM.changeSign(residual)// r = -( A * x)
        CommonOps_DDRM.addEquals(residual, bMat) // r = -(A * x) + b

        CommonOps_DDRM.multTransA(A, residual, g)  // g = A^T * r
        CommonOps_DDRM.changeSign(g)
        val stepSize = 1e-2 // tune this
        for (i in 0 until x.numRows) {
            val updated = x.get(i, 0) - stepSize * g.get(i, 0)
            x.set(i, 0, maxOf(0.0, updated))  // projection to ≥ 0
        }
    }
    val out = x.data
    return x.data
}
fun nnlsSparkOld(A: DMatrixRMaj, b: DoubleArray): DoubleArray {
    val m = A.numRows
    val n = A.numCols
    require(b.size == m)
    val bMat = DMatrixRMaj(m, 1, true, *b)
    // atb = A^T b

    val atbMat = DMatrixRMaj(n, 1)
    CommonOps_DDRM.multTransA(A, bMat, atbMat) // n×1

    // ata = A^T A
    val ataMat = DMatrixRMaj(n, n)
    CommonOps_DDRM.multTransA(A, A, ataMat)    // n×n

    val ataColMajor = toColumnMajor(ataMat)
    val atb = atbMat.data.copyOf() // already a flat vector length n

    val ws = NNLS.createWorkspace(n)
    return NNLS.solve(ataColMajor, atb, ws)
}
private fun nnlsSparkSparse(A: DMatrixSparseCSC, b: DoubleArray): DoubleArray {
    val m = A.numRows
    val n = A.numCols
    require(b.size == m)

    val bMat = DMatrixRMaj(m, 1, true, *b)

    // A^T b  (sparse × dense)
    val atbMat = DMatrixRMaj(n, 1)
    CommonOps_DSCC.multTransA(A, bMat, atbMat, null)

    // A^T A  (transpose sparse, then sparse × sparse)
    val aT = CommonOps_DSCC.transpose(A, null, null)
    val ataSparse = CommonOps_DSCC.mult(aT, A, null, null, null)

    // NNLS still wants dense normal equations
    val ataMat = DMatrixRMaj(n, n)
    DConvertMatrixStruct.convert(ataSparse, ataMat)

    return solveNormalEquations(ataMat, atbMat)
}

private fun solveNormalEquations(
    ataMat: DMatrixRMaj,
    atbMat: DMatrixRMaj
): DoubleArray {
    val n = ataMat.numCols
    val ataColMajor = toColumnMajor(ataMat)
    val atb = atbMat.data.copyOf()

    val ws = NNLS.createWorkspace(n)
    return NNLS.solve(ataColMajor, atb, ws)
}
fun toColumnMajor(a: DMatrixRMaj): DoubleArray {
    val m = a.numRows
    val n = a.numCols
    val out = DoubleArray(m * n)
    var k = 0
    for (c in 0 until n) {
        for (r in 0 until m) {
            out[k++] = a.get(r, c)
        }
    }
    return out
}