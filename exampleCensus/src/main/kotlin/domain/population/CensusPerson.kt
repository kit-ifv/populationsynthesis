package edu.kit.ifv.populationsynthesis.domain.population

import edu.kit.ifv.populationsynthesis.input.population.Employment

data class CensusPerson constructor(
    val age: Int,
    val sex: Sex = Sex.MALE,
    val employment: Employment = Employment.UNKNOWN,
    val graduationCode: Int = -1,
) {

}
const val GERMANY_ADULT_AGE = 18
val Collection<CensusPerson>.numberOfAdults: Int get() = filter { it.age >= GERMANY_ADULT_AGE }.size
val Collection<CensusPerson>.numberOfChildren: Int get() = filter { it.age < GERMANY_ADULT_AGE }.size
