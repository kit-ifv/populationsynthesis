package edu.kit.ifv.populationsynthesis.optimizers.ortools

import com.google.ortools.Loader

object ORSolverFactory {
    init {
        Loader.loadNativeLibraries()
    }

    fun lenient(lambda: ORFactoryConfig.() -> Unit): LenientORToolsSolver {
        val config = ORFactoryConfig().also(lambda)
        return LenientORToolsSolver(config.solver, config.upperBound)
    }

    fun direct(lambda: ORFactoryConfig.() -> Unit): DirectORToolsSolver {
        val config = ORFactoryConfig().also(lambda)
        return DirectORToolsSolver(config.solver, config.upperBound)
    }

    fun withWeights(
        scale: Double = 1.0,
        lambda: ORFactoryConfig.() -> Unit
    ): WeightRegardingLenientORToolsSolver {
        val config = ORFactoryConfig().also(lambda)
        return WeightRegardingLenientORToolsSolver(
            config.solver,
            config.upperBound,
            scale,
        )
    }

    val direct by lazy { direct {} }
    val lenient by lazy { lenient {} }

}