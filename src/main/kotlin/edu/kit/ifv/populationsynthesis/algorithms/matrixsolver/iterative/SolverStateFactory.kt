package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.toVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import kotlin.time.TimeSource

fun interface SolverStateFactory<S: SolverState> {
    fun create(vectors: Collection<ScalableVector>, observers: Collection<RuleObserver>): S

    companion object {
        val ipuState = SolverStateFactory<ObserverTrackingState> {vectors ,observers ->
            IPUState(observers.toList(), vectors.toList())
        }
        val ipuStateNormalized = SolverStateFactory<ObserverTrackingState> {vectors ,observers ->
            IPUState(observers.toList(), vectors.toList()).apply {
                normalize()
            }
        }

        val noNormalization = SolverStateFactory<MutableResidualState> { a, b ->
            ResidualSolverState(b.toVector(), a.toList())
        }

        val normalization = SolverStateFactory<MutableResidualState> { a, b ->
            ResidualSolverState(b.toVector(), a.toList()).apply {
                normalize()
            }
        }
            }
}

interface ObserverTrackingState: SolverState {
    val observers: Collection<RuleObserver>
}

data class IPUState(
    override val observers: Collection<RuleObserver>,
    override val vectors: List<ScalableVector>,
): MutableSolverState, ObserverTrackingState {
    override val spawnMark: TimeSource.Monotonic.ValueTimeMark = TimeSource.Monotonic.markNow()
    override val target: Vector = observers.toVector()
}