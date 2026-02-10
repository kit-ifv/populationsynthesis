package edu.kit.ifv.populationsynthesis.rules.covered

fun interface ExhaustiveMeasurementSetSupplier<T> : MeasurementSetSupplier<T> {
    fun generateAllDescriptions(): FullDescriptorGroup<T> = FullDescriptorGroup(generateMeasurements())
}

