package edu.kit.ifv.populationsynthesis.rules.measurement

import edu.kit.ifv.populationsynthesis.rules.Rule

interface MeasurementDefinition<in T> {
    fun createNamedMeasurement(): NamedMeasurement<T>

    /**
     * Important: The string returned by this method is used for equality checks for the [NamedMeasurement].
     * Please make sure that objects return the same description if and only if their evaluation behaviour is
     * identical.
     */
    fun generateDescription(): String
    fun makeRule(target: Number, description: String? = null): Rule<T> = Rule(
        target = target.toDouble(),
        logic = createNamedMeasurement(),
        description = description
    )

    fun makeOptionalRule(target: Number?, description: String? = null): Rule<T>? {
        return target?.let { makeRule(it, description) }
    }

}