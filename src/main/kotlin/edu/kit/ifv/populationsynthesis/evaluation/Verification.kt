package edu.kit.ifv.populationsynthesis.evaluation

import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider


object Verification {
    fun <AREA, T> verify(
        hierarchicRuleProvider: HierarchicRuleProvider<AREA, T>,
        output: Map<AREA, Collection<T>>
    ): List<RuleOutput> {
        val hierarchy = hierarchicRuleProvider.hierarchy
        return hierarchicRuleProvider.getAllRules().flatMap { (area, ruleSet) ->
            val allLeafsFrom = hierarchy.getAllLeafsFrom(area)
            val relevantOutput = output.filterKeys { it in allLeafsFrom }.values.flatten() + output[area].orEmpty()
            ruleSet.map { rule ->

                RuleOutput(area.toString(), rule.identifier, rule.target, rule.total(relevantOutput))
            }
        }
    }
}
