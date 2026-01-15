package edu.kit.ifv.populationsynthesis.rules.contribution

import edu.kit.ifv.populationsynthesis.rules.NamedContribution

abstract class NumericContributionDefinition<T>: ContributionDefinition<T> {
    abstract fun generateDescription(): String
    abstract fun evaluation(element: T): Number

    final override fun createNamedContribution() : NamedContribution<T> {
        return NamedContribution.Companion.numeric(generateDescription(), logic = ::evaluation)
    }
}