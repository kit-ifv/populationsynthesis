package edu.kit.ifv.populationsynthesis.rules.measurement

/**
 * A boolean contribution definition for an element [T] compares (T) -> Boolean to check whether the element
 * fulfills whatever criterion the implementor wants to test. The [NamedMeasurement] will receive the name of
 * the [generateDescription] function. Developers should ensure that the returned String will be the same when the
 * evaluation function would evaluate identical.
 */
abstract class BooleanMeasurementDefinition<T> : MeasurementDefinition<T> {
    /**
     * Important: The string returned by this method is used for equality checks for the [NamedMeasurement].
     * Please make sure that objects return the same description if and only if their evaluation behaviour is
     * identical.
     */
    abstract fun generateDescription(): String
    abstract fun evaluation(element: T): Boolean

    final override fun createNamedMeasurement(): NamedMeasurement<T> {
        return NamedMeasurement.Companion.boolean(generateDescription(), logic = ::evaluation)
    }
}