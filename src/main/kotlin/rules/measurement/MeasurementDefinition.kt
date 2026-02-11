package edu.kit.ifv.populationsynthesis.rules.measurement

import edu.kit.ifv.populationsynthesis.rules.Rule

interface MeasurementDefinition<T> {
    fun createNamedMeasurement(): NamedMeasurement<T>


    fun makeRule(target: Number, description: String? = null): Rule<T> = Rule(
        target = target.toDouble(),
        logic = createNamedMeasurement(),
        description = description
    )
}