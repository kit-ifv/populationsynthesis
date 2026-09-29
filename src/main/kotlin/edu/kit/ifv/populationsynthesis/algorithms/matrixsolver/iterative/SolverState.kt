package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import kotlin.time.TimeSource

interface SolverState {
    val target: Vector
    val vectors: Collection<Vector>
    val spawnMark: TimeSource.Monotonic.ValueTimeMark

}


