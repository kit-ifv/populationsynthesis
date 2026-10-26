package edu.kit.ifv.populationsynthesis.domain.population

import edu.kit.ifv.populationsynthesis.rules.CensusHouseholdType

data class CensusHousehold(
    val id: HouseholdID = HouseholdID.INVALID,
    val members: List<CensusPerson>
) {

    val seniorityStatus = SeniorityStatus.fromPersons(members)
    val type = CensusHouseholdType.fromPersons(members)
    constructor(vararg ages: Int) : this(members = ages.map { CensusPerson(it) })
}




enum class SeniorityStatus {
    ONLY_SENIORS, MIXED, NO_SENIORS;


    companion object {

        private const val SENIOR_AGE = 65 // TODO cross check
        fun fromPersons(persons: Collection<CensusPerson>): SeniorityStatus {
            return when {
                persons.all { it.age >= SENIOR_AGE } -> ONLY_SENIORS
                persons.none { it.age >= SENIOR_AGE } -> NO_SENIORS
                else -> MIXED
            }
        }
    }
}