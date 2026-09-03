package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.datastructures.Signature
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.WeightedElement
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.DirectRunReport
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.MatrixSolver
import edu.kit.ifv.populationsynthesis.algorithms.sampling.ListSampler
import edu.kit.ifv.populationsynthesis.algorithms.sampling.PickWithReplacement
import kotlin.random.Random


typealias IPUCallback = (DirectRunReport) -> Unit

typealias MatrixSolution =  List<WeightedElement<Signature>>
data class HierarchicDistributionConfig(
    val matrixSolver: MatrixSolver = MatrixSolver.legacy,
//    val refinement: Refinement = Refinement { },
//    val signatureDistributor: InitialSignatureDistributor = GreedyAmountDistro(),
    val random: Random = Random(42),
    val samplerAlgorithm: ListSampler = PickWithReplacement(random),
    val initialVectorCallback: (List<ScalableVector>) -> Unit = {},
    val matrixSolutionCallback: (List<ScalableVector>, List<RuleObserver>) -> Unit = { vectors, observers ->

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
