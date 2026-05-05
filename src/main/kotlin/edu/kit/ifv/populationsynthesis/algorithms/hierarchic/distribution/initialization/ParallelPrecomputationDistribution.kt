package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.initialization

import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.MutableSignatureAmount
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.Partition
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.SignatureAmount
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.SignatureIndex
import edu.kit.ifv.populationsynthesis.datastructures.toCyclicMutableList
import it.unimi.dsi.fastutil.PriorityQueue
import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

class ParallelPrecomputationDistribution(
    val insertionMetric: PartitionMetric = OptimizedSquaredDiff,
) : InitialSignatureDistributor {
    override fun distribute(partitions: List<Partition>, signatureAmounts: Collection<SignatureAmount>) {


        val operationSet = signatureAmounts.toMutableAmounts().toMutableSet()
        require(operationSet.size == signatureAmounts.size) {
            "Cannot create a set, there are some elements with duplicate signature."


        }
        operationSet.removeAll{it.atomicAmount.get() == 0}

        val helpers = partitions.map { spawnHelper(it) }.toCyclicMutableList()
        runBlocking {updateHelpersInParallel(helpers, operationSet)}
//                helpers.parallelStream().forEach { it.update(operationSet) }

        while(helpers.isNotEmpty()) {
            require(operationSet.all { it.atomicAmount.get() > 0 }) {
                "This is a fail"
            }
            val currentPartition = helpers.next()
            if(currentPartition.cannotBeImproved()) {
                helpers.remove(currentPartition)
                continue
            }
            val element = currentPartition.tryUpdate()


            if (element != null) {
                operationSet.remove(element)
                if(operationSet.isEmpty()) {
                    return
                }
                runBlocking {updateHelpersInParallel(helpers, operationSet)}
//                helpers.parallelStream().forEach { it.update(operationSet) }
                helpers.decrementIndex()
            } else if (currentPartition.elementIsEmpty()) {
                operationSet.remove(currentPartition.element())
                if(operationSet.isEmpty()) {
                    return
                }
                runBlocking {updateHelpersInParallel(helpers, operationSet)}
//                helpers.parallelStream().forEach { it.update(operationSet) }

            }

            if(element == null) {
                currentPartition.forceUpdate(operationSet)
            }
        }
        operationSet.toMutableList().assignEmergency(partitions)



    }
    private suspend fun updateHelpersInParallel(
        helpers: Collection<Helper>,
        operationSet:  Set<MutableSignatureAmount>
    ) = coroutineScope {
        helpers.forEach { helper ->
            launch(Dispatchers.Default) {
                helper.update(operationSet)
            }
        }
    }
    fun MutableList<MutableSignatureAmount>.assignEmergency(regions: List<Partition>) {
        var i = 0
        while (isNotEmpty()) {
            val current = first()
            val bestRegion = regions.maxBy { it.evaluateMetric(current.index.index, insertionMetric) }
            bestRegion.takeOne(current)
            if (current.atomicAmount.get() <= 0) {
                remove(current)
            }
            i++
        }
    }



    private fun spawnHelper(partition: Partition): Helper {
        return Helper(insertionMetric, partition)
    }

    private fun spawnHelper2(partition: Partition, initialElements: Collection<MutableSignatureAmount>): Helper2 {
        return Helper2(insertionMetric, TrackedPartition(partition, insertionMetric, initialElements))
    }

    private class Helper2(
        private val insertionMetric: PartitionMetric,
        private val partition: TrackedPartition
    ) {

    }

    private class Helper(
        private val insertionMetric: PartitionMetric,
        private val partition: Partition,
        private var bestElement: MutableSignatureAmount? = null,
        private var bestMetric: Double = -1.0
    )
    {
        fun update(potentialTargets: Set<MutableSignatureAmount>) {
            if(bestElement in potentialTargets) {
                return
            }
            forceUpdate(potentialTargets)

        }
        fun forceUpdate(potentialTargets: Set<MutableSignatureAmount>) {
            bestElement = potentialTargets.maxBy { partition.evaluateMetric(it.index.index,insertionMetric) }
            bestMetric = partition.evaluateMetric(bestElement!!.index.index, insertionMetric)
        }
        fun metrics(potentialTargets: Set<MutableSignatureAmount>) = potentialTargets.map { it to partition.evaluateMetric(it.index.index,insertionMetric) }.sortedByDescending { it.second }
        fun element() = bestElement!!
        fun cannotBeImproved() = bestMetric <0.0
        fun elementIsEmpty() = (bestElement?.atomicAmount?.get() ?:0) <= 0
        fun tryUpdate(): MutableSignatureAmount? {
            val bestElement = bestElement!!
            val value = bestElement.atomicAmount.andDecrement
            if(value <= 0 ) return bestElement
            partition.delta(bestElement.index, 1)
//            val message = "Partition ${partition.id} Taking $bestElement"
//            println(message)
//            log(message)

            return null
        }
    }

    private class TrackedPartition(
        private val partition: Partition,
        private val insertionMetric: PartitionMetric,
        initialSignatures: Collection<MutableSignatureAmount>
    ) {

        private val queue: PriorityQueue<Candidate> = ObjectHeapPriorityQueue<Candidate>(
            Comparator.comparingDouble<Candidate> { it.score }.reversed()
        )

        init {
            initialSignatures.forEach {
                queue.enqueue(Candidate(it, partition.evaluateMetric(it.index.index,insertionMetric )))
            }
        }


        fun update(): MutableSignatureAmount {

            while (!queue.isEmpty) {
                val target = queue.dequeue()
                if (target.target.atomicAmount.get() <= 0) {
                    continue
                }
                val currentMeasurement = partition.evaluateMetric(
                    target.target.index.index,
                    insertionMetric
                )

                if (target.score == currentMeasurement) {
                    return target.target
                }

                queue.enqueue(Candidate(target.target, currentMeasurement))
            }

            error("No available candidate")
        }
        fun delta(signature: SignatureIndex, amount: Int) {
            partition.delta(signature, amount)
        }
    }

    private class Candidate(
        val target: MutableSignatureAmount,
        val score: Double,
    )


}

private val logFile = File("parallelpartitionmarne.log")
private fun log(message: String) {
    logFile.appendText(message + System.lineSeparator())
}

fun Collection<SignatureAmount>.toMutableAmounts(): Collection<MutableSignatureAmount> {
    return withIndex().map { (i, v) -> v.toMutable(i) }
}