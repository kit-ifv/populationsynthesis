package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.Signature

data class SignatureAmount(
    val signature: Signature,
    val amount: Int,
) {
    fun toMutable(index: Int): MutableSignatureAmount {
        return MutableSignatureAmount(signature, amount, SignatureIndex(index))
    }
}