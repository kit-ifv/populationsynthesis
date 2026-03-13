package edu.kit.ifv.populationsynthesis.rules.measurement

/**
 * An insertion point for contribution functions that take a numeric measurement from the element to determine its
 * contribution.
 */
abstract class NumericMeasurementDefinition<T> : MeasurementDefinition<T> {
    abstract fun evaluation(element: T): Number

    final override fun createNamedMeasurement(): NamedMeasurement<T> {
        return NamedMeasurement.numeric(generateDescription(), logic = ::evaluation)
    }
}