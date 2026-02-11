package edu.kit.ifv.populationsynthesis.measurements

import edu.kit.ifv.populationsynthesis.datasource.CensusPerson
import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition

/**
 * Measurement definition that evaluates whether a single person falls within a given age range.
 *
 * This definition operates on [CensusPerson] directly and returns a boolean result indicating
 * whether the person’s age lies within the specified [range].
 *
 * Defining the measurement at the person level is intentionally more general than defining it
 * at the household level:
 * - The same logic can be reused in different aggregation contexts (e.g. households, teams,
 *   organizations, or other collections of people).
 * - Higher-level measurements (such as household-based counts) can be derived by aggregating
 *   this definition rather than duplicating the logic.
 *
 * While this approach requires an additional composition step when used at the household level,
 * it improves reuse and keeps the core measurement semantics narrowly focused.
 */
class PersonAgeDefinition(val range: IntRange) : BooleanMeasurementDefinition<CensusPerson>() {
    /**
     * Convenience constructor that creates an open-ended age range starting at [lowerBound].
     */
    constructor(lowerBound: Int) : this(lowerBound..Int.MAX_VALUE)

    override fun generateDescription(): String {
        return "Age in [${range.first}..${range.last}]"
    }

    override fun evaluation(element: CensusPerson): Boolean {
        return element.age in range
    }
}

