package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.measurement.Measurement
import edu.kit.ifv.populationsynthesis.rules.measurement.NamedMeasurement

class Rule<in T> constructor(
    val target: Double,

    val logic: NamedMeasurement<T>,
    description: String? = null,
) {

    val identifier: LogicIdentifier = logic.identifier
    val description = description?: identifier.text
    fun measure(element: T): Double = logic.measure(element)
    fun total(elements: Collection<T>): Double = elements.sumOf { measure(it) }

    fun appliesTo(element: T): Boolean = measure(element) != 0.0

    fun delta(output: Collection<T>): Double = target - total(output)

    operator fun plus(double: Double): Rule<T> = Rule(target + double, logic =  logic)

    override fun toString(): String {
        return "Rule(target=$target, logic=${logic.identifier})"
    }

}

