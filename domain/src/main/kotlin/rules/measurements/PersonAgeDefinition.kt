package rules.measurements

import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition
import population.HasAge
import population.Person
import population.age

class PersonAgeDefinition(val acceptedRange: IntRange): BooleanMeasurementDefinition<Person<HasAge>>() {
    override fun generateDescription(): String {
        return "Person age in $acceptedRange"
    }

    override fun evaluation(element: Person<HasAge>): Boolean {
        return element.age in acceptedRange
    }
}