package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU


data class HierarchicDistributionConfig(
    val ipu: GenericIPU = GenericIPU.legacy,
//    val refinement: Refinement = Refinement { },
//    val signatureDistributor: InitialSignatureDistributor = GreedyAmountDistro(),
//    val ipuCalculationCallback: (List<Pair<Rule<*>, Double>>) -> Unit = {},
)
