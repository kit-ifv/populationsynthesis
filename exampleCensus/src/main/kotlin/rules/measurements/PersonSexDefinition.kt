package edu.kit.ifv.populationsynthesis.rules.measurements

import edu.kit.ifv.populationsynthesis.domain.population.Sex
import edu.kit.ifv.populationsynthesis.domain.population.CensusPerson
import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition

class PersonSexDefinition(val expectedSex: Sex): BooleanMeasurementDefinition<CensusPerson>() {
    override fun generateDescription(): String {
        return "Sex equals $expectedSex"
    }

    override fun evaluation(element: CensusPerson): Boolean {
        return element.sex == expectedSex
    }
}