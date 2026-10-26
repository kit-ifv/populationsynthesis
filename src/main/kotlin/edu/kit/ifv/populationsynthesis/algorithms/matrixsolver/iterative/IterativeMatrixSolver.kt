package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.MatrixSolver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector

interface IterativeMatrixSolver<S: SolverState> : MatrixSolver{
    fun initialize(vectors: Collection<ScalableVector>, observers: Collection<RuleObserver>): S
    fun iteration(state: S, iteration: Int)
    fun exitCriterion(state: S, iteration: Int): Boolean
    fun finish(state: S) = state.finish()
    override fun solve(vectors: Collection<ScalableVector>, observers: Collection<RuleObserver>) {
        val solver = initialize(vectors, observers)
        var counter = 0
        while(!exitCriterion(solver, counter)) {
            iteration(solver, counter)
            counter++
        }
        finish(solver)

    }

}



