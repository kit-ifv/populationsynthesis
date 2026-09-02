package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver

import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector

@Deprecated("Imprecise naming, use MatrixSolver instead")
fun interface GenericIPU: MatrixSolver {
    fun run(vectors: Collection<ScalableVector>, observers: Collection<RuleObserver>)
    override fun solve(vectors: Collection<ScalableVector>, observers: Collection<RuleObserver>) {
        run(vectors, observers)
    }
}