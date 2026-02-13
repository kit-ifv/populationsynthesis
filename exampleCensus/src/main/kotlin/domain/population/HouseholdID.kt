package edu.kit.ifv.populationsynthesis.domain.population

@JvmInline
value class HouseholdID(val value: Long) {
    companion object {
        val INVALID = HouseholdID(-1)
    }
}