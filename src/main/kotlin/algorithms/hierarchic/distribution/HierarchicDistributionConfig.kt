package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU


typealias IPUCallback = (List<RuleObserver>) -> Unit

val CONSOLE_PRINT: IPUCallback = {
    it.forEach {
        val output = "(${it.expected} ${it.actual})"
        println(output)

    }

}

data class HierarchicDistributionConfig(
    val ipu: GenericIPU = GenericIPU.legacy,
//    val refinement: Refinement = Refinement { },
//    val signatureDistributor: InitialSignatureDistributor = GreedyAmountDistro(),
    val ipuCalculationCallback: IPUCallback = {},
)
