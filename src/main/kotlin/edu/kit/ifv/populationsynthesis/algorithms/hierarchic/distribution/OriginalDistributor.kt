package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.initialization.GreedyDistribution
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.initialization.InitialSignatureDistributor
import edu.kit.ifv.populationsynthesis.algorithms.sampling.ListSampler
import edu.kit.ifv.populationsynthesis.datastructures.Signature
import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.rules.LogicIndexer
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider

class OriginalDistributor<AREA, T>(
    private val ruleProvider: HierarchicRuleProvider<AREA, in T>,
    private val logicIndexer: LogicIndexer<AREA, in T>,
    private val householdMapping: Map<Signature, List<T>>,
    private val sampler: ListSampler,
) : Distributor<AREA, T> {
    private val hierarchy: HierarchicElement<AREA> = ruleProvider.hierarchy


    override fun distribute(

        initialSolution: List<SignatureAmount>,
        rootArea: AREA
    ): Map<AREA, List<T>> {

        val output: MutableMap<AREA, List<T>> = mutableMapOf()
        val handledNonTargetNodes = mutableMapOf<AREA, List<SignatureAmount>>()
        val targetAreas = hierarchy.getAllLeafsFrom(rootArea)
        if (targetAreas.isNotEmpty()) {
            handledNonTargetNodes[rootArea] = initialSolution
        } else {
            output[rootArea] = unpack(initialSolution, householdMapping)
        }

        while (handledNonTargetNodes.isNotEmpty()) {
            val (area, signatureAmounts) = handledNonTargetNodes.entries.first()
            handledNonTargetNodes.remove(area)
            val distribution = distributeToImmediateChildren(area, signatureAmounts)
            distribution.entries.forEach { (subArea, amounts) ->
                if (subArea !in targetAreas) {
                    handledNonTargetNodes[subArea] = amounts
                } else {
                    output[subArea] = unpack(amounts, householdMapping)
                }
            }
        }
        return output
    }

    fun unpack(
        amounts: Collection<SignatureAmount>,
        signatures: Map<Signature, List<T>>,
    ): List<T> {

        return amounts.flatMap { (signature, amount) ->
            val elements = signatures[signature]?: emptyList()
            sampler.sample(elements, amount)
        }
    }

    private fun distributeToImmediateChildren(
        area: AREA,
        targetAmounts: List<SignatureAmount>
    ): Map<AREA, List<SignatureAmount>> {
        val childAreas = hierarchy.getImmediateChildren(area)
        val signatures = targetAmounts.map { it.signature }
        if (childAreas.isEmpty()) throw IllegalStateException("We expect at least one child for distribution")
        require(signatures.none{it.isEmpty()}) {
            "That is an invalid signature, how did that get created"
        }
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