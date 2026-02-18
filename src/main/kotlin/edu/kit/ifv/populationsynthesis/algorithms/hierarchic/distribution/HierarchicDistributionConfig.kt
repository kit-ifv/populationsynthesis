package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.algorithms.ipu.DirectRunReport
import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU


typealias IPUCallback = (DirectRunReport) -> Unit

val CONSOLE_PRINT: IPUCallback = {
    it.observers.forEach {
        val output = "(${it.expected} ${it.actual})"
        println(output)

    }

}

data class HierarchicDistributionConfig(
    val ipu: GenericIPU = GenericIPU.legacy,
//    val refinement: Refinement = Refinement { },
//    val signatureDistributor: InitialSignatureDistributor = GreedyAmountDistro(),
    val ipuCalculationCallback: IPUCallback = {
        if(it.amountOfZeroVectors > 0) println("There are degenerate vectors in the IPU. This happens when a seed element matches no rule, the vectors will be set to 0")
    },
)
