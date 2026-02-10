package edu.kit.ifv.populationsynthesis.rules.measurement

/**
 * An insertion point for contribution functions that take a numeric measurement from the element to determine its
 * contribution.
 */
abstract class NumericMeasurementDefinition<T> : MeasurementDefinition<T> {
    /**
     * Important: The string returned by this method is used for equality checks for the [NamedMeasurement].
     * Please make sure that objects return the same description if and only if their evaluation behaviour is
     * identical.
     */
    abstract fun generateDescription(): String
    abstract fun evaluation(element: T): Number

    final override fun createNamedMeasurement(): NamedMeasurement<T> {
        return NamedMeasurement.Companion.numeric(generateDescription(), logic = ::evaluation)
    }
}