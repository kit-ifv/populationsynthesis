package edu.kit.ifv.populationsynthesis.rules.contribution

/**
 * An insertion point for contribution functions that take a numeric measurement from the element to determine its
 * contribution.
 */
abstract class NumericContributionDefinition<T> : ContributionDefinition<T> {
    /**
     * Important: The string returned by this method is used for equality checks for the [NamedContribution].
     * Please make sure that objects return the same description if and only if their evaluation behaviour is
     * identical.
     */
    abstract fun generateDescription(): String
    abstract fun evaluation(element: T): Number

    final override fun createNamedContribution(): NamedContribution<T> {
        return NamedContribution.Companion.numeric(generateDescription(), logic = ::evaluation)
    }
}