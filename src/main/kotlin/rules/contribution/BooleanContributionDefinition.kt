package edu.kit.ifv.populationsynthesis.rules.contribution

/**
 * A boolean contribution definition for an element [T] compares (T) -> Boolean to check whether the element
 * fulfills whatever criterion the implementor wants to test. The [NamedContribution] will receive the name of
 * the [generateDescription] function. Developers should ensure that the returned String will be the same when the
 * evaluation function would evaluate identical.
 */
abstract class BooleanContributionDefinition<T>: ContributionDefinition<T> {
    /**
     * Important: The string returned by this method is used for equality checks for the [NamedContribution].
     * Please make sure that objects return the same description if and only if their evaluation behaviour is
     * identical.
     */
    abstract fun generateDescription(): String
    abstract fun evaluation(element: T): Boolean

    final override fun createNamedContribution() : NamedContribution<T> {
        return NamedContribution.Companion.boolean(generateDescription(), logic = ::evaluation)
    }
}