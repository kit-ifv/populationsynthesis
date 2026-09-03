package edu.kit.ifv.populationsynthesis.runs

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.OptimizationSolver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.ipu.CyclicIPU
import edu.kit.ifv.populationsynthesis.optimizers.ojalgo.OJSolverBuilder
import kotlin.io.path.Path

fun main() {

    val runConfig = RunConfig()

    runConfig.execute {
        scenario = Scenario.BAVARIA
        matrixSolver = CyclicIPU()
        outputPath = Path("output/PopulationBavaria.csv")
        verificationPath = Path("output/ExampleBavaria.csv")
    }


    runConfig.execute {
        scenario = Scenario.BAVARIA
        matrixSolver = OptimizationSolver(OJSolverBuilder())
        outputPath = Path("output/PopulationBavaria.csv")
        verificationPath = Path("output/ExampleBavaria.csv")
    }

}