package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.Signature

data class MutableSignatureAmount(
    val signature: Signature,
    var amount: Int,
    val index: SignatureIndex,
)