package edu.kit.ifv.populationsynthesis.rules.measurement

/**
 * A boolean contribution definition for an element [T] compares (T) -> Boolean to check whether the element
 * fulfills whatever criterion the implementor wants to test. The [NamedMeasurement] will receive the name of
 * the [generateDescription] function. Developers should ensure that the returned String will be the same when the
 * evaluation function would evaluate identical.
 */
abstract class BooleanMeasurementDefinition<T> : MeasurementDefinition<T> {

    abstract fun evaluation(element: T): Boolean

    final override fun createNamedMeasurement(): NamedMeasurement<T> {
        return NamedMeasurement.boolean(generateDescription(), logic = ::evaluation)
    }
}