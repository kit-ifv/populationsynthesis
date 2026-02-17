package population.householdtype

import population.HasAge
import population.HasSex
import population.Household
import population.age
import population.sex

object StandardHouseholdTypeDetector : HouseholdTypeDetector {
    override fun <P> detectHouseholdType(household: Household<out P>): HouseholdType where P : HasAge, P : HasSex {
        if(household.size == 1) return HouseholdType.SINGLE

        val (adults, children) = household.members.partition { it.age > 18 }
        if(adults.size == 1 && children.isNotEmpty()) return HouseholdType.SINGLE_PARENT
        if(adults.size == 2 && adults[0].sex != adults[1].sex) {
            if(children.isEmpty()) return HouseholdType.COUPLE_NOCHILDS
            return HouseholdType.COUPLE_WITHCHILDS
        }
        return HouseholdType.MULTIHOUSEHOLD

    }

}