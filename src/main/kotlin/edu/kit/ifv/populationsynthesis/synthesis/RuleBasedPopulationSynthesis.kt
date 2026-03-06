package edu.kit.ifv.populationsynthesis.synthesis

import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider

interface RuleBasedPopulationSynthesis<AREA, T> : GenericPopulationSynthesis<AREA, T> {
    val ruleProvider: RuleProvider<AREA, T>

    /**
     * The rule provider has a set of registered areas anyways, so we can implement convenience method.
     *
     */
    fun synthesizeAll(): Map<AREA, List<T>>
}