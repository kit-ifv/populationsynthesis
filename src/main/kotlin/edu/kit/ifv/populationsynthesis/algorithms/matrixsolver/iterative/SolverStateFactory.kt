package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state.IPUState
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state.MutableResidualState
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state.ObserverTrackingState
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state.ResidualSolverState
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.toVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector

fun interface SolverStateFactory<S: SolverState> {
    fun create(vectors: Collection<ScalableVector>, observers: Collection<RuleObserver>): S

    companion object {
        val ipuState = SolverStateFactory<ObserverTrackingState> { vectors, observers ->
            IPUState(observers.toList(), vectors.toList())
        }
        val ipuStateNormalized = SolverStateFactory<ObserverTrackingState> { vectors, observers ->
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

