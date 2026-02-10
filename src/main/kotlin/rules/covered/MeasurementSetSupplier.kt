package edu.kit.ifv.populationsynthesis.rules.covered

import edu.kit.ifv.populationsynthesis.rules.measurement.NamedMeasurement

fun interface MeasurementSetSupplier<T> {
    fun generateMeasurements(): List<NamedMeasurement<T>>
}