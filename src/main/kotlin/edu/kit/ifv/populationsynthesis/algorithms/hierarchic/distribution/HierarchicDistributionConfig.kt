package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.Signature
import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.WeightedElement
import edu.kit.ifv.populationsynthesis.algorithms.ipu.DirectRunReport
import edu.kit.ifv.populationsynthesis.algorithms.ipu.MatrixSolver
import edu.kit.ifv.populationsynthesis.algorithms.ipu.toVector
import edu.kit.ifv.populationsynthesis.algorithms.percentError
import edu.kit.ifv.populationsynthesis.algorithms.sum


typealias IPUCallback = (DirectRunReport) -> Unit

typealias MatrixSolution =  List<WeightedElement<Signature>>
data class HierarchicDistributionConfig(
    val matrixSolver: MatrixSolver = MatrixSolver.legacy,
//    val refinement: Refinement = Refinement { },
//    val signatureDistributor: InitialSignatureDistributor = GreedyAmountDistro(),
    val matrixSolutionCallback: (List<ScalableVector>, List<RuleObserver>) -> Unit = { vectors, observers ->

        val message = vectors.sum()
        val diff = observers.toVector() - message
        println("-----Matrix Solver Print -----")

        println(message)
        println(diff.length())
        println(diff)
        println(observers.map { it.absoluteDifference })
        println(observers.map { it.percentError })
        println("----- /* End Matrix Sl*/ -----")
    },
) {
    companion object {
        val CONSOLE_PRINT: IPUCallback = { runReport ->
            runReport.observers.forEach {
                val output = "(${it.expected} ${it.actual})"
                println(output)

            }

        }
    }
}
