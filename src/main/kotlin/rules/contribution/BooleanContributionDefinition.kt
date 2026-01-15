package edu.kit.ifv.populationsynthesis.rules.contribution

import edu.kit.ifv.populationsynthesis.rules.NamedContribution

// TODO set an interface above these classes, so that another injection point for Contribution creation is given.
abstract class BooleanContributionDefinition<T>: ContributionDefinition<T> {

    abstract fun generateDescription(): String
    abstract fun evaluation(element: T): Boolean

    final override fun createNamedContribution() : NamedContribution<T> {
        return NamedContribution.Companion.boolean(generateDescription(), logic = ::evaluation)
    }
}