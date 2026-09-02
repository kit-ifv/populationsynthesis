package edu.kit.ifv.populationsynthesis.runs

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.JosephineStepper
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.VectorTransformations
import edu.kit.ifv.populationsynthesis.datastructures.linalg.sum
import edu.kit.ifv.populationsynthesis.optimizers.ojalgo.OJSolverBuilder
import edu.kit.ifv.populationsynthesis.optimizers.ortools.ORSolverFactory
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.OptimizationSolver
import kotlin.io.path.Path
import kotlin.system.exitProcess



fun main() {



    val runConfig = RunConfig()
    var lastDistance = Double.MAX_VALUE
    runConfig.execute {
        matrixSolver = JosephineStepper(
//            exitCriterion = {target, vectors ->
//                val sum = vectors.sum()
////                println(sum)
//                val newLength = (target - sum).length()
////                println(newLength)
//                if(lastDistance - newLength < 0.0001) {
//                    true
//                } else {
//                    lastDistance = newLength
//                    false
//                }
//            }
        )
        outputPath = Path("output/MarneMARS.csv")
        verificationPath = Path("output/VerificationMarneMARS.csv")
    }

    runConfig.execute {

        matrixSolver = OptimizationSolver(OJSolverBuilder())
        outputPath = Path("output/MarneOJSolve.csv")
        verificationPath = Path("output/VerificationMarneOJSolve.csv")
    }
    exitProcess(0)
    runConfig.execute {
        matrixSolver = JosephineStepper(vectorTransformations = VectorTransformations.NORMALIZE_ELEMENT_VECTORS)
        outputPath = Path("output/MarneMARSPercent.csv")
        verificationPath = Path("output/VerificationMarneMARSPercent.csv")
    }







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