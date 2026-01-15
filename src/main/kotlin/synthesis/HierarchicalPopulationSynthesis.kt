package edu.kit.ifv.populationsynthesis.synthesis

import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider

abstract class HierarchicalPopulationSynthesis<AREA, H>(

    override val ruleProvider: HierarchicRuleProvider<AREA, H>
) : RuleBasedPopulationSynthesis<AREA, H> {

    val hierarchy get() = ruleProvider.hierarchy


    override fun synthesizeAll(): Map<AREA, List<H>> {
        return synthesize(hierarchy.getAllLeafs())
    }

}