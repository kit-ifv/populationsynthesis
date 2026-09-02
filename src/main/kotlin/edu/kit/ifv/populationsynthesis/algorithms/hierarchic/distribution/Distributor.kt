package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

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