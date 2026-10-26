package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state.MutableResidualState
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state.ObserverTrackingState

object Solvers {

    fun iterativeProportionalUpdate(lambda: SolverConfiguration<ObserverTrackingState>.() -> Unit = {}): DefaultIterativeMatrixSolver<ObserverTrackingState> {
        return create{
            step = IterationStep.ipuStep
            stateFactory = SolverStateFactory.ipuStateNormalized
            apply(lambda)
        }
    }


    fun cyclicCoordinateDescent(lambda: SolverConfiguration<MutableResidualState>.() -> Unit = {}): DefaultIterativeMatrixSolver<MutableResidualState> {
        return create {
            step = IterationStep.ccdStep
            stateFactory = SolverStateFactory.normalization
            apply(lambda)
        }
    }


    fun <S: SolverState> create(lambda: SolverConfiguration<S>.() -> Unit = {}): DefaultIterativeMatrixSolver<S> {
        val solverConfiguration = SolverConfiguration<S>().apply(lambda)
        return solverConfiguration.build()
    }

    val IPU get()= iterativeProportionalUpdate()
    val CCD get() = cyclicCoordinateDescent()
}