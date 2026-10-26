package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver

import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.synthesis.MatrixTask

class MatrixSolverWithCallback(
    val original: MatrixSolver,
    val callback: (MatrixTask) -> Unit
): MatrixSolver {
    override fun solve(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ) {
        val task = MatrixTask(vectors, observers)
        original.solve(task)
        callback(task)
    }
}

fun MatrixSolver.withCallback(callback: (MatrixTask) -> Unit): MatrixSolverWithCallback {
    return MatrixSolverWithCallback(this, callback)
}