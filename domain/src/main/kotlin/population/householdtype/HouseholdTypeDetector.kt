package population.householdtype

import population.HasAge
import population.HasSex
import population.Household

interface HouseholdTypeDetector {
    fun <P> detectHouseholdType(household: Household<out P>): HouseholdType where P : HasAge,
                                                                                  P : HasSex
}