package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.algorithms.ipu.DirectRunReport
import edu.kit.ifv.populationsynthesis.algorithms.ipu.MatrixSolver


typealias IPUCallback = (DirectRunReport) -> Unit


data class HierarchicDistributionConfig(
    val matrixSolver: MatrixSolver = MatrixSolver.legacy,
//    val refinement: Refinement = Refinement { },
//    val signatureDistributor: InitialSignatureDistributor = GreedyAmountDistro(),
    val ipuCalculationCallback: IPUCallback = {
        if (it.amountOfZeroVectors > 0) println("There are degenerate vectors in the IPU. This happens when a seed element matches no rule, the vectors will be set to 0")
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
