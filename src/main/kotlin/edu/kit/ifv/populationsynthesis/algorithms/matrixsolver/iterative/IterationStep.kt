package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state.ObserverTrackingState
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state.ResidualSolverState
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state.ResidualState
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.toVector

fun interface IterationStep<in S: SolverState> {
    fun step(state: S, iteration: Int)

    companion object {
        val none = IterationStep<SolverState> { _, _->

        }

        val ipuStep = IterationStep<ObserverTrackingState> { state, iteration ->
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
        val ccdStep = IterationStep<ResidualState> { state, _ ->
            state.vectors.forEach { vector ->
                val scaling = state.leastSquaresScalar(vector)
                val delta = scaling.coerceAtLeast(-vector.scalar)
                vector.scalar += delta
            }
        }

        fun dampedCcdStep(dampFactor: Double) = IterationStep<ResidualState> { state, _ ->
            state.vectors.forEach { vector ->
                val scaling = state.leastSquaresScalar(vector) * dampFactor
                val delta = scaling.coerceAtLeast(-vector.scalar)
                vector.scalar += delta
            }
        }
        fun dampedRelativeCcdStep(dampFactor: Double) = IterationStep<ResidualState> { state, _ ->
            state.vectors.forEach { vector ->
                val temporaryScaling = state.leastSquaresScalar(vector)
                val scaling = temporaryScaling.coerceIn(-vector.scalar / dampFactor, vector.scalar * dampFactor)
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