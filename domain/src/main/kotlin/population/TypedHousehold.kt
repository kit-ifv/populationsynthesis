package population

import population.householdtype.HouseholdTypeDomain

interface TypedHousehold<T>: Household<T> {
    val type: HouseholdTypeDomain
}