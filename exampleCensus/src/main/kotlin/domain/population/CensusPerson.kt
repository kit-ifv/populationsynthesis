package edu.kit.ifv.populationsynthesis.domain.population

data class CensusPerson(
    val age: Int,
    val sex: Sex = Sex.MALE,
) {

}