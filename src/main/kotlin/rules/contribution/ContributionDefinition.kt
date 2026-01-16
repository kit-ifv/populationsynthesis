package edu.kit.ifv.populationsynthesis.rules.contribution

import edu.kit.ifv.populationsynthesis.rules.contribution.NamedContribution
import edu.kit.ifv.populationsynthesis.rules.Rule

interface ContributionDefinition<T> {
    fun createNamedContribution(): NamedContribution<T>

    fun makeRule(target: Double): Rule<T> = Rule(target, createNamedContribution())
}