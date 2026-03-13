package edu.kit.ifv.populationsynthesis.synthesis

import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider

interface RuleBasedPopulationSynthesis<AREA, T> : CompletePopulationSynthesis<AREA, T> {
    val ruleProvider: RuleProvider<AREA, in T>
}

