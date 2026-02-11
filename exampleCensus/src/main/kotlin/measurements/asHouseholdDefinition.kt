package edu.kit.ifv.populationsynthesis.measurements

import edu.kit.ifv.populationsynthesis.datasource.CensusHousehold
import edu.kit.ifv.populationsynthesis.datasource.CensusPerson
import edu.kit.ifv.populationsynthesis.rules.measurement.MeasurementDefinition
import edu.kit.ifv.populationsynthesis.rules.measurement.NumericMeasurementDefinition

/**
 * Theoretically a measurement on a person can easily be extended to a measurement of a household
 * by applying the measurement to each member.
 *
 * This is advanced stuff and only necessary for high level code deduplication. In most instances it is entirely
 * sufficient to define measurements over the object that you are measuring.
 */
fun MeasurementDefinition<CensusPerson>.asHouseholdDefinition(): MeasurementDefinition<CensusHousehold> {
    val original = createNamedMeasurement()
    return object : NumericMeasurementDefinition<CensusHousehold>() {
        override fun generateDescription(): String {
            return "${original.identifier.text} for Household"
        }

        override fun evaluation(element: CensusHousehold): Number {
            return element.members.sumOf { original.measure(it) }
        }

    }
}