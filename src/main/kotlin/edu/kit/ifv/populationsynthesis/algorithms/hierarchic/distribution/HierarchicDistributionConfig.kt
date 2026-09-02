package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.datastructures.Signature
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.WeightedElement
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.DirectRunReport
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.MatrixSolver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.toVector
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.percentError
import edu.kit.ifv.populationsynthesis.algorithms.sampling.ListSampler
import edu.kit.ifv.populationsynthesis.algorithms.sampling.PickWithReplacement
import edu.kit.ifv.populationsynthesis.datastructures.linalg.sum
import kotlin.random.Random


typealias IPUCallback = (DirectRunReport) -> Unit

typealias MatrixSolution =  List<WeightedElement<Signature>>
data class HierarchicDistributionConfig(
    val matrixSolver: MatrixSolver = MatrixSolver.legacy,
//    val refinement: Refinement = Refinement { },
//    val signatureDistributor: InitialSignatureDistributor = GreedyAmountDistro(),
    val random: Random = Random(42),
    val samplerAlgorithm: ListSampler = PickWithReplacement(random),
    val matrixSolutionCallback: (List<ScalableVector>, List<RuleObserver>) -> Unit = { vectors, observers ->

        val message = vectors.sum()
        val diff = observers.toVector() - message
        println()
        println("-----Matrix Solver Print -----")
        println("Vector scalars: ${vectors.map { it.scalar }}")
        println("Vector vSum: $message")
        println("||t - vSum||2 = ${diff.length()}")
        println("delta $diff")
        println(observers.map { it.absoluteDifference })
        println(observers.map { it.percentError })
        println("-----Vector information ------")
        val sum = vectors.sumOf { it.scalar }
        val text = vectors.map { it.scalar / sum }
        println(text)
        println(text.min())
        println(text.max())
        println("----- /* End Matrix Sl*/ -----")
        println()
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
