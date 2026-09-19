package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver

import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.optimizers.LoadMatrixProblemToSolver

class OptimizationSolver<T>(
    private val model: LoadMatrixProblemToSolver<T>
): MatrixSolver {
    override fun solve(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ) {
        vectors.forEach { vector ->
            model.addVectorVariable(vector)
        }
        observers.forEach { observer ->
            model.addObserver(observer)
        }
        model.afterConfigHook()
        val (resultLookup, bonus) = model.solve()
        vectors.forEach { vector ->
            vector.scalar = resultLookup[vector] ?: .0.also {
                println("Could not find scalar for vector $vector")
            }
        }
    }

}