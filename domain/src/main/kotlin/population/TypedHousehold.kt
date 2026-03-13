package population

import population.householdtype.HouseholdType

interface TypedHousehold<out T>: Household<T> {
    val type: HouseholdType
}