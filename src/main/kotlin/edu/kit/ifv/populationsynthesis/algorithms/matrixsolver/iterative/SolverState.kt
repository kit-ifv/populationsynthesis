package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import edu.kit.ifv.populationsynthesis.utils.NumericProbabilityDistribution
import kotlin.time.TimeSource

interface SolverState {
    val target: Vector
    val vectors: Collection<ScalableVector>
    val spawnMark: TimeSource.Monotonic.ValueTimeMark
    fun debugInformation(originalDistribution: NumericProbabilityDistribution?) = SolverStateInfo.fromSolverState(this, originalDistribution)
    fun finish() {

    }
}


