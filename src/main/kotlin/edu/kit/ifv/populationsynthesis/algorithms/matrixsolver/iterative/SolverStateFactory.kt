package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.toVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import kotlin.time.TimeSource

fun interface SolverStateFactory<S: SolverState> {
    fun create(vectors: Collection<ScalableVector>, observers: Collection<RuleObserver>): S

    companion object {
        val ipuState = SolverStateFactory<IPUState> {vectors ,observers ->
            IPUState(observers.toList(), vectors.toList())
        }
        val ipuStateNormalized = SolverStateFactory<IPUState> {vectors ,observers ->
            IPUState(observers.toList(), vectors.toList()).apply {
                normalize()
            }
        }

    }
}

data class IPUState(
    val observers: Collection<RuleObserver>,
    override val vectors: List<ScalableVector>,
): MutableSolverState {
    override val spawnMark: TimeSource.Monotonic.ValueTimeMark = TimeSource.Monotonic.markNow()
    override val target: Vector = observers.toVector()
}