package population.householdtype

import population.HasAge
import population.HasBiologicalSex
import population.Household
import population.age
import population.sex

object StandardHouseholdTypeDetector : HouseholdTypeDetector {
    override fun <P> detectHouseholdType(household: Household<out P>): HouseholdTypeDomain where P : HasAge, P : HasBiologicalSex {
        if(household.size == 1) return HouseholdTypeDomain.SINGLE

        val (adults, children) = household.members.partition { it.age > 18 }
        if(adults.size == 1 && children.isNotEmpty()) return HouseholdTypeDomain.SINGLE_PARENT
        if(adults.size == 2 && adults[0].sex != adults[1].sex) {
            if(children.isEmpty()) return HouseholdTypeDomain.COUPLE_NOCHILDS
            return HouseholdTypeDomain.COUPLE_WITHCHILDS
        }
        return HouseholdTypeDomain.MULTIHOUSEHOLD

    }

}