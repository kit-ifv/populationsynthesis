package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.toVector

fun interface IterationStep<in S: SolverState> {
    fun step(state: S, iteration: Int)

    companion object {
        val none = IterationStep<SolverState> { _, _->

        }

        val ipuStep = IterationStep<ObserverTrackingState> {state, iteration ->
            state.observers.forEach {
                it.optimize()
            }
        }

        val noNormalization = SolverStateFactory { a, b ->
            ResidualSolverState(b.toVector(), a.toList())
        }

        val normalization = SolverStateFactory { a, b ->
            ResidualSolverState(b.toVector(), a.toList()).apply {
                normalize()
            }
        }
        val ccdStep = IterationStep<ResidualState> {state, _ ->
            state.vectors.forEach { vector ->
                val scaling = state.leastSquaresScalar(vector)
                val delta = scaling.coerceAtLeast(-vector.scalar)
                vector.scalar += delta
            }
        }
        val unclampedCcdStep = IterationStep<ResidualState> { state, _ ->
            state.vectors.forEach { vector ->
                val scaling = state.leastSquaresScalar(vector)
                vector.scalar += scaling
            }
        }


    }
}