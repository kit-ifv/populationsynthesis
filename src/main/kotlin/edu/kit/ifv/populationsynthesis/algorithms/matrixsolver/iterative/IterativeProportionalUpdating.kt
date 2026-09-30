package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class IterativeProportionalUpdating(
    maxIter: Int = 1000,
    maxDuration: Duration = 30.minutes,
    private val convergenceCriterion: Double = 1e-8,
    solverStateFactory: SolverStateFactory<IPUState> = SolverStateFactory.ipuStateNormalized,
) : AbstractIterativeMatrixSolver<IPUState>(maxIter, maxDuration, solverStateFactory) {

    override fun step(
        state: IPUState,
        iteration: Int
    ) {
        state.observers.forEach {
            it.optimize()

        }
    }
    override fun finish(state: IPUState) {
        return // Nothing to do, observer manipulation results in the vectors having the right scalars loaded anyways.
    }
}