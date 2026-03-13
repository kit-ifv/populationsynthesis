package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.initialization

import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.MutableSignatureAmount
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.Partition
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.SignatureAmount

class GreedyDistribution(
    val insertionMetric: PartitionMetric = SquaredDiff,
) : InitialSignatureDistributor {
    override fun distribute(partitions: List<Partition>, signatureAmounts: Collection<SignatureAmount>) {
        val elements = signatureAmounts.withIndex().map { it.value.toMutable(it.index) }.toMutableList()
        elements.removeAll { it.amount == 0 } // Don't need to bother evaluating signatures that should not be placed
        elements.assignGreedy(partitions)
        elements.assignEmergency(partitions)
    }

    @Suppress("CognitiveComplexMethod", "MagicNumber")
    fun MutableList<MutableSignatureAmount>.assignGreedy(partitions: List<Partition>) {
        var i = 0
        var improvementFound = true
        val removeablePartitions = partitions.toMutableList()
        while (isNotEmpty()) {
            if (removeablePartitions.isEmpty()) {
                return
            }
            val loopedIndex = i % removeablePartitions.size // Iterate through the active partitions.
            if (loopedIndex == 0) {
                // If one cycle of assignments has found no change, then no partition wants to take in the remaining elements
                // In this instance, the assignment strategy should also be terminated, and the remaining elements be assigned
                // via whatever strategy is used for that.
                if (!improvementFound) return
                improvementFound = false
            }
            val currentPartition = removeablePartitions[loopedIndex]
            val bestElement = maxBy { currentPartition.evaluateMetric(it.index.index, insertionMetric) }
            val potentialElement =
                if (currentPartition.evaluateMetric(bestElement.index.index, insertionMetric) < 0) null else bestElement
            potentialElement?.let {
                currentPartition.takeOne(it)
                i++
                improvementFound = true
                if (it.amount <= 0) {
                    remove(it)
                }
            } ?: run {
                // Emergency, if the 0 indexed element is removed, the other elements may still gain benefits and we should not terminate early
                if (loopedIndex == 0) improvementFound = true
                removeablePartitions.remove(currentPartition)
            }
        }
    }

    @Suppress("MagicNumber")
    fun MutableList<MutableSignatureAmount>.assignEmergency(regions: List<Partition>) {
        var i = 0
        while (isNotEmpty()) {
            val current = first()
            val bestRegion = regions.maxBy { it.evaluateMetric(current.index.index, insertionMetric) }
            bestRegion.takeOne(current)
            if (current.amount <= 0) {
                remove(current)
            }
            i++
        }
    }
}


fun Partition.takeOne(signatureAmount: MutableSignatureAmount) {
    delta(signatureAmount.index, 1)
    signatureAmount.amount--
}