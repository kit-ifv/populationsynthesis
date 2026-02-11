package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.initialization

import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.Partition
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.SignatureAmount

fun interface InitialSignatureDistributor {
    fun distribute(partitions: List<Partition>, signatureAmounts: Collection<SignatureAmount>)
}