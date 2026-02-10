package edu.kit.ifv.populationsynthesis.rules.measurement

import edu.kit.ifv.populationsynthesis.rules.Rule

interface MeasurementDefinition<T> {
    fun createNamedMeasurement(): NamedMeasurement<T>

    fun makeRule(target: Double): Rule<T> = Rule(target, createNamedMeasurement())
}