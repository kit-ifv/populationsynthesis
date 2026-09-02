package edu.kit.ifv.populationsynthesis.datastructures.linalg

import edu.kit.ifv.populationsynthesis.datastructures.Signature

class SignatureScalableVector(override val signature: Signature, override var scalar: Double = 1.0) : ScalableVector {

    override fun currentValueForIndex(index: Int): Double {
        return signature[index] * scalar
    }

    override fun attributeForIndex(index: Int): Double {
        return signature[index]
    }

    override fun appliesToRule(ruleIndex: Int): Boolean {
        return signature.hasKey(ruleIndex)
    }


}