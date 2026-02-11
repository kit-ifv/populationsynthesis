package edu.kit.ifv.populationsynthesis.algorithms.ipu

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.lsqr
import org.apache.spark.mllib.optimization.NNLS
import org.ejml.data.DMatrixRMaj
import org.ejml.dense.row.CommonOps_DDRM

class SparkNNLS: GenericIPU {
    override fun run(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ) {
        val mtx= vectors.toMatrix()
        val b = observers.map { it.expected }.toDoubleArray()
        val solution = nnlsSpark(mtx, b)
        vectors.zip(solution.toList()).forEach {(vector, calculatedScalar) ->
            vector.scalar = calculatedScalar
        }
    }

}

fun nnlsSpark(A: DMatrixRMaj, b: DoubleArray): DoubleArray {
    val m = A.numRows
    val n = A.numCols
    require(b.size == m)

    // atb = A^T b
    val bMat = DMatrixRMaj(m, 1, true, *b)
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