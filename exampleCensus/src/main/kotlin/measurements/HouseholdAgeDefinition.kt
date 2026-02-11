package edu.kit.ifv.populationsynthesis.measurements

import edu.kit.ifv.populationsynthesis.datasource.CensusHousehold
import edu.kit.ifv.populationsynthesis.rules.measurement.NumericMeasurementDefinition

/**
 * Measurement definition that counts how many members of a household fall within a given age range.
 *
 * This definition is functionally equivalent to applying [PersonAgeDefinition] to each household
 * member and aggregating the results by summation.
 *
 * Unlike [PersonAgeDefinition], which produces a boolean result per person, this definition
 * produces a numeric measurement over [edu.kit.ifv.populationsynthesis.datasource.CensusHousehold], representing the number of matching
 * household members.
 *
 * This is the most common form used when constructing household-level rules, as it directly
 * expresses the quantity of interest without requiring additional aggregation logic at the call site.
 */
class HouseholdAgeDefinition(val range: IntRange) : NumericMeasurementDefinition<CensusHousehold>() {

    /**
     * Convenience constructor that creates an open-ended age range starting at [lowerBound].
     */
    constructor(lowerBound: Int) : this(lowerBound..Int.MAX_VALUE)


    /**
     * Generates a description that uniquely identifies the measurement logic.
     *
     * Descriptions must be identical for logically equivalent measurements and must differ
     * for measurements with different evaluation behavior. Reusing a description for different
     * logic will lead to incorrect rule identification.
     */
    override fun generateDescription(): String {
        return "Household Members Age in [${range.first}..${range.last}]"
    }
    /**
     * Evaluates the household by counting how many members fall within the target age range.
     */
    override fun evaluation(element: CensusHousehold): Number {
        return element.members.count {
            it.age in range
        }
    }
}