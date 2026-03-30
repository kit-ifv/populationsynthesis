package edu.kit.ifv.populationsynthesis.algorithms.ipu

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.lsqr
import org.ejml.data.DMatrixRMaj
import org.ejml.data.DMatrixSparseCSC
import org.ejml.ops.DConvertMatrixStruct
import java.io.File
import kotlin.io.path.Path

object LeastSquareRegression : GenericIPU {
    override fun run(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ) {
        val mtx = vectors.toMatrix()
        val b = observers.map { it.expected }.toDoubleArray()
        val solution = lsqr(mtx, b).x
        vectors.zip(solution.toList()).forEach { (vector, calculatedScalar) ->
            vector.scalar = calculatedScalar
        }

    }
}

fun Collection<ScalableVector>.toSparseMatrix(): DMatrixSparseCSC {
    val maxSize = maxOf { it.highestIndex() }

    val mtx = DMatrixSparseCSC(maxSize, size)

    forEachIndexed { i, vector ->
        vector.signature.forEachEntry { j, value ->
            mtx[j, i] = value
        }
    }
    return mtx
}

fun Collection<ScalableVector>.toMatrix(): DMatrixRMaj {
    val maxSize = maxOf { it.highestIndex() }

    val mtx = DMatrixSparseCSC(maxSize, size)

    forEachIndexed { i, vector ->
        vector.signature.forEachEntry { j, value ->
            mtx[j, i] = value
        }
    }
    val dense = DMatrixRMaj(maxSize, size)
    val out = DConvertMatrixStruct.convert(mtx, dense)
    return out
}

fun Collection<RuleObserver>.toVector(): DoubleArray {
    return map { it.expected }.toDoubleArray()
}

fun dumpToCSV(A: DMatrixRMaj, file: File = Path("dump.csv").toFile()) {
    val mapper = CsvMapper()

    val schema = CsvSchema.builder()
        .setUseHeader(false)
        .build()

    val rows = (0 until A.numRows).map { r ->
        (0 until A.numCols).map { c -> A.get(r, c) }
    }

    mapper.writer(schema).writeValue(file, rows)

}
