package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.GenericCollector
import edu.kit.ifv.populationsynthesis.Signature
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.initialization.GreedyDistribution
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.initialization.InitialSignatureDistributor
import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.rules.LogicIndexer
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider

/**
 * The distributor is tasked with taking
 */
fun interface Distributor<AREA, out T> {

    fun distribute(

        initialSolution: List<SignatureAmount>,
        rootArea: AREA,
    ): Map<AREA, List<T>>
}


class OriginalDistributor<AREA, T>(
    private val ruleProvider: HierarchicRuleProvider<AREA, in T>,
    private val logicIndexer: LogicIndexer<AREA, in T>,
    private val householdMapping: Map<Signature, List<T>>,
) : Distributor<AREA, T> {
    private val hierarchy: HierarchicElement<AREA> = ruleProvider.hierarchy


    override fun distribute(

        initialSolution: List<SignatureAmount>,
        rootArea: AREA
    ): Map<AREA, List<T>> {

        val output: MutableMap<AREA, List<T>> = mutableMapOf()
        val handledNonTargetNodes = mutableMapOf<AREA, List<SignatureAmount>>()
        val targetAreas = hierarchy.getAllChildren(rootArea)
        if (targetAreas.isNotEmpty()) {
            handledNonTargetNodes[rootArea] = initialSolution
        } else {
            output[rootArea] = finalize(initialSolution, householdMapping)
        }

        while (handledNonTargetNodes.isNotEmpty()) {
            val (area, signatureAmounts) = handledNonTargetNodes.entries.first()
            handledNonTargetNodes.remove(area)
            val distribution = distributeToImmediateChildren(area, signatureAmounts)
            distribution.entries.forEach { (subArea, amounts) ->
                if (subArea !in targetAreas) {
                    handledNonTargetNodes[subArea] = amounts
                } else {
                    output[subArea] = finalize(amounts, householdMapping)
                }
            }
        }
        return output
    }

    private val collector = GenericCollector<SignatureAmount, T> {
        it.map { it.amount }
    }

    fun finalize(
        amounts: Collection<SignatureAmount>,
        signatures: Map<Signature, List<T>>,
    ): List<T> {
        val targetMap = amounts.associateWith {
            val targetHouseholds = signatures[it.signature] ?: run {
                error("No households for the signature ${it.signature}")
            }
            targetHouseholds
        }
        return collector.extract(targetMap).map { it }
    }

    private fun distributeToImmediateChildren(
        area: AREA,
        targetAmounts: List<SignatureAmount>
    ): Map<AREA, List<SignatureAmount>> {
        val childAreas = hierarchy.getImmediateChildren(area)
        val signatures = targetAmounts.map { it.signature }
        if (childAreas.isEmpty()) throw IllegalStateException("We expect at least one child for distribution")

        val signatureTracker = SignatureTracker(signatures, signatures.maxOf {
            it.maxKey
        } + 1)

        val partitions = childAreas.map { region ->
            logicIndexer.createPartition(region, signatureTracker, ruleProvider)
        }
        val initialDistribution: InitialSignatureDistributor = GreedyDistribution()
        initialDistribution.distribute(partitions, targetAmounts)

        return childAreas.zip(partitions).associate { (region, partition) ->
            region to partition.output()
        }
    }


}

fun <AREA> LogicIndexer<AREA, *>.createPartition(
    target: AREA,
    signatureTracker: SignatureTracker,
    ruleProvider: HierarchicRuleProvider<AREA, *>
): Partition {
    val rules = ruleProvider.getComposedRules(target)
    val expectedValues = rules.associate {
        getIndex(it) to it.target
    }
    val targetArray = DoubleArray(size) { 0.0 }
    val maskArray = BooleanArray(size) { false }
    expectedValues.forEach { (index, target) ->
        targetArray[index] = target
        maskArray[index] = true

    }
    return Partition(targetArray, signatureTracker, maskArray)

}