package population

import population.householdtype.HouseholdTypeDomain

interface TypedHousehold<out T>: Household<T> {
    val type: HouseholdTypeDomain
}