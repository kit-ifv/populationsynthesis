package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.toVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import kotlin.time.TimeSource

data class IPUState(
    override val observers: List<RuleObserver>,
    override val vectors: List<ScalableVector>,
): MutableSolverState, ObserverTrackingState {
    override val spawnMark: TimeSource.Monotonic.ValueTimeMark = TimeSource.Monotonic.markNow()
    override val target: Vector = observers.toVector()
}