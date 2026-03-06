package rules.measurements

import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition
import population.HasAge
import population.HasBiologicalSex
import population.Person
import population.Sex
import population.age
import population.sex

class PersonAgeSexDefinition<T>(val acceptedAgeRange: IntRange, val acceptedSex: Sex) :
    BooleanMeasurementDefinition<Person<T>>() where T : HasAge, T : HasBiologicalSex {
    override fun generateDescription(): String {
        return "Person age in $acceptedAgeRange && sex == $acceptedSex"
    }

    override fun evaluation(element: Person<T>): Boolean {
        return element.age in acceptedAgeRange && element.sex == acceptedSex
    }
}