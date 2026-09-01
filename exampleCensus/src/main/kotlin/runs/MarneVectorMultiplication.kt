package edu.kit.ifv.populationsynthesis.runs

import edu.kit.ifv.populationsynthesis.algorithms.ipu.JosephineStepper
import edu.kit.ifv.populationsynthesis.algorithms.ipu.VectorTransformations
import edu.kit.ifv.populationsynthesis.optimizers.OJSolverBuilder
import edu.kit.ifv.populationsynthesis.optimizers.ORSolverFactory
import edu.kit.ifv.populationsynthesis.optimizers.OptimizationSolver
import kotlin.io.path.Path
import kotlin.system.exitProcess

fun main() {

    val runConfig = RunConfig()

    runConfig.execute {
        matrixSolver = JosephineStepper(vectorTransformations = VectorTransformations.NORMALIZE_ELEMENT_VECTORS)
        outputPath = Path("output/MarneMARSPercent.csv")
        verificationPath = Path("output/VerificationMarneMARSPercent.csv")
    }
    exitProcess(0)
    runConfig.execute {
        matrixSolver = JosephineStepper()
        outputPath = Path("output/MarneMARS.csv")
        verificationPath = Path("output/VerificationMarneMARS.csv")
    }



    exitProcess(0)


    runConfig.execute {
        matrixSolver = JosephineStepper(vectorTransformations = VectorTransformations.PERCENTAGE_TRANSFORMATION)
        outputPath = Path("output/MarneMARS2Percent.csv")
        verificationPath = Path("output/VerificationMarneMARS2Percent.csv")
    }

    runConfig.execute {

        matrixSolver = OptimizationSolver(ORSolverFactory.lenient)
        outputPath = Path("output/MarneORSolve.csv")
        verificationPath = Path("output/VerificationMarneORSolve.csv")
    }

    runConfig.execute {

            matrixSolver = OptimizationSolver(OJSolverBuilder())
            outputPath = Path("output/MarneOJSolve.csv")
            verificationPath = Path("output/VerificationMarneOJSolve.csv")
    }







}