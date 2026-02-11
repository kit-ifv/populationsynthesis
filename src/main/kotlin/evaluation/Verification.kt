package edu.kit.ifv.populationsynthesis.evaluation

import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider


class Verification() {
    fun <AREA, T> verify(hierarchicRuleProvider: HierarchicRuleProvider<AREA, T>, output: Map<AREA, Collection<T>>): List<RuleOutput<AREA>> {
        val hierarchy = hierarchicRuleProvider.hierarchy
        return hierarchicRuleProvider.getAllRules().flatMap { (area, ruleSet) ->
            val allLeafsFrom = hierarchy.getAllLeafsFrom(area)
            val relevantOutput = output.filterKeys { it in allLeafsFrom }.values.flatten() + output[area].orEmpty()
            ruleSet.map { rule ->

                RuleOutput(area, rule.identifier, rule.target, rule.total(relevantOutput))
            }
        }
    }
}

data class RuleOutput<AREA>(
    val area: AREA,
    val logicIdentifier: LogicIdentifier,
    val total: Double,
    val actual: Double
) {
    override fun toString(): String {
        return "${total - actual} ${area.toString()} ${logicIdentifier.text}"
    }
}