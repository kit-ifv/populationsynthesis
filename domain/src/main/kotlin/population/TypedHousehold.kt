package population

import population.householdtype.HouseholdType

interface TypedHousehold<T>: Household<T> {
    val type: HouseholdType
}