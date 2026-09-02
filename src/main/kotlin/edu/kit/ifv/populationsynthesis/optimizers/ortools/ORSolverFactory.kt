package edu.kit.ifv.populationsynthesis.optimizers.ortools

import com.google.ortools.Loader

object ORSolverFactory {
    init {
        Loader.loadNativeLibraries()
    }
    fun lenient(lambda: ORFactoryConfig.() -> Unit): ORToolsSolver {
        val config = ORFactoryConfig().also(lambda)
        return LenientORToolsSolver(config.solver, config.upperBound)
    }
    fun direct(lambda: ORFactoryConfig.() -> Unit): ORToolsSolver {
        val config = ORFactoryConfig().also(lambda)
        return DirectORToolsSolver(config.solver, config.upperBound)
    }
    val direct  by lazy {direct {}}
    val lenient by lazy {lenient {}}

}