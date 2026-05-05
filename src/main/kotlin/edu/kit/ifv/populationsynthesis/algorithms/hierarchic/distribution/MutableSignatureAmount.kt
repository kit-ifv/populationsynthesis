package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.Signature
import java.util.concurrent.atomic.AtomicInteger

class MutableSignatureAmount(
    val signature: Signature,
    amount: Int,
    val index: SignatureIndex,
) {
    val atomicAmount = AtomicInteger(amount)

    override fun toString(): String {
        return "MutableSignature(index=$index, amount=${atomicAmount.get()}"
    }
}