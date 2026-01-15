package edu.kit.ifv.populationsynthesis.synthesis

import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider

interface RuleBasedPopulationSynthesis<AREA, H> : GenericPopulationSynthesis<AREA, H> {
    val ruleProvider: RuleProvider<AREA, H>

    /**
     * The rule provider has a set of registered areas anyways, so we can implement convenience method.
     *
     */
    fun synthesizeAll(): Map<AREA, List<H>>
}