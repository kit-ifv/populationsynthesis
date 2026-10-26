package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state.IPUState
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state.MutableResidualState
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state.ObserverTrackingState
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state.ResidualSolverState
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.toVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.isRelevantFor

fun interface SolverStateFactory<S : SolverState> {

    /**
     * Constructs a solver state from vectors that have already
     * been filtered for relevance.
     *
     * Implementations may override [create] if irrelevant vectors
     * need to be retained.
     */
    fun createFromRelevant(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ): S

    /**
     * Creates a solver state, excluding vectors that cannot
     * contribute to any observer.
     *
     * Excluded vectors have their scalars set to zero.
     * Override this method if the solver requires them.
     */
    fun create(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ): S {
        val (relevant, irrelevant) = vectors.partition { it.isRelevantFor(observers) }
        irrelevant.forEach { vector -> vector.scalar = .0 }

        return createFromRelevant(relevant, observers)
    }

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

