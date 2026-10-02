package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import kotlin.time.Duration

fun interface IterationObserver<in S: SolverState> {
    fun observe(iteration: Int, duration: Duration, state: S)
}