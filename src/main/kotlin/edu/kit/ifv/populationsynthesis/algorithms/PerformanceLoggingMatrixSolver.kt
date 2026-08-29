package edu.kit.ifv.populationsynthesis.algorithms

import edu.kit.ifv.populationsynthesis.algorithms.ipu.MatrixSolver
import java.nio.file.Path
import kotlin.io.path.appendText
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.writeText
import kotlin.time.measureTime

data class PerformanceLoggingMatrixSolver(val original: MatrixSolver, val path: Path) : MatrixSolver {
    override fun solve(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ) {

        val duration = measureTime { original.solve(vectors, observers) }
        val text = "$original; ${vectors.size}; ${observers.size}; $duration\n"
        if (path.exists()) path.appendText(text) else {
            path.parent.createDirectories()
            path.writeText(text)
        }

    }
}