package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state.MutableResidualState
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state.ObserverTrackingState
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class SolverConfiguration<S : SolverState>(
    var maxIter: Int = 1000,
    var maxDuration: Duration = 30.minutes,
) {
    lateinit var step: IterationStep<S>
    lateinit var stateFactory: SolverStateFactory<S>
    fun build(): DefaultIterativeMatrixSolver<S> {
        return DefaultIterativeMatrixSolver(
            maxIter = maxIter,
            maxDuration = maxDuration,
            step = step,
            stateFactory = stateFactory,
        )
    }

    companion object {
        // Deliberately getter and not a field so that each config is "fresh"
        val ipuConfig get()= SolverConfiguration<ObserverTrackingState>().apply {
            step = IterationStep.ipuStep
            stateFactory = SolverStateFactory.ipuStateNormalized
        }

        val ccdConfig get()= SolverConfiguration<MutableResidualState>().apply {
            step = IterationStep.ccdStep
            stateFactory = SolverStateFactory.normalization
        }
    }
}