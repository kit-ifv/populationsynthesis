package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

data class PotentialTransfer(
    val signatureIndex: SignatureIndex,
    val impact: Double,
    var amountOfElements: Int,
) {
    fun isEmpty(): Boolean = amountOfElements <= 0
}