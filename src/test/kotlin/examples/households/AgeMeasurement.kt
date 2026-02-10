package examples.households

import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition

data class AgeMeasurement(
    val range: IntRange,
) : BooleanMeasurementDefinition<TestPerson>() {
    override fun evaluation(element: TestPerson): Boolean {
        return element.age in range
    }


    override fun generateDescription(): String = toString()
}