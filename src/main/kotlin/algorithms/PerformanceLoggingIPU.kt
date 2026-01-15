package edu.kit.ifv.populationsynthesis.algorithms

import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU
import java.nio.file.Path
import kotlin.io.path.appendText
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.writeText
import kotlin.time.measureTime

data class PerformanceLoggingIPU(val original: GenericIPU, val path: Path): GenericIPU {
    override fun run(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ) {

        val duration = measureTime { original.run(vectors, observers) }
        val text = "$original; ${vectors.size}; ${observers.size}; $duration\n"
        if(path.exists()) path.appendText(text) else {
            path.parent.createDirectories()
            path.writeText(text)
        }

    }
}