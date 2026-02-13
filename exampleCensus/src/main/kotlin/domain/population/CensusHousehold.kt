package edu.kit.ifv.populationsynthesis.domain.population

data class CensusHousehold(
    val id: HouseholdID = HouseholdID.INVALID,
    val members: List<CensusPerson>
) {
    constructor(vararg ages: Int) : this(members = ages.map { CensusPerson(it) })
}

