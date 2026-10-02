package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

//class IterativeProportionalUpdating(
//    maxIter: Int = 1000,
//    maxDuration: Duration = 30.minutes,
//    private val step: IterationStep<ObserverTrackingState> = ipuStep,
//    solverStateFactory: SolverStateFactory<ObserverTrackingState> = SolverStateFactory.ipuStateNormalized,
//) : DefaultIterativeMatrixSolver<ObserverTrackingState>(maxIter, maxDuration, solverStateFactory) {
//
//    override fun step(
//        state: ObserverTrackingState,
//        iteration: Int
//    ) {
//        step.step(state, iteration)
//    }
//    override fun finish(state: ObserverTrackingState) {
//        return // Nothing to do, observer manipulation results in the vectors having the right scalars loaded anyways.
//    }
//
//}
//
//
