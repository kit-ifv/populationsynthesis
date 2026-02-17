package rules.measurements

import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition
import population.*
import population.householdtype.HouseholdType

class HouseholdTypeDefinition(val targetType: HouseholdType): BooleanMeasurementDefinition<TypedHousehold<*>>() {
    override fun generateDescription(): String {
        return "Household Type == $targetType"
    }

    override fun evaluation(element: TypedHousehold<*>): Boolean {
        return element.type == targetType
    }
}


