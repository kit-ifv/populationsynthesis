package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.domain.population.CensusPerson
import edu.kit.ifv.populationsynthesis.domain.population.numberOfAdults
import edu.kit.ifv.populationsynthesis.domain.population.numberOfChildren
import edu.kit.ifv.populationsynthesis.input.rules.CensusHouseholdData
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.covered.ExplicitTargetCoverageGroup
import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition

object HHTypeRuleFactory {

    fun build(input: CensusHouseholdData): CoverageGroup<CensusHousehold> {
        val ruleSet = CensusHouseholdType.entries.mapNotNull { optionalEqualityRule(it, input) }.toRuleSet()
        return ExplicitTargetCoverageGroup(rules = ruleSet, target = requireNotNull(input.Insgesamt_val))
    }

    private fun optionalEqualityRule(type: CensusHouseholdType, input: CensusHouseholdData): Rule<CensusHousehold>? {
        return HouseholdTypeDefinition(type).makeOptionalRule(input.getHouseholdTypeTarget(type))
    }
}


class HouseholdTypeDefinition(val householdType: CensusHouseholdType): BooleanMeasurementDefinition<CensusHousehold>() {
    override fun evaluation(element: CensusHousehold): Boolean {
        return element.type == householdType
    }

    override fun generateDescription(): String {
        return "Household Type == $householdType"
    }
}


enum class CensusHouseholdType {
    SINGLE, COUPLE_NO_KIDS, COUPLE_WITH_KIDS, SINGLE_PARENT, OTHER_MULTICORE;


    companion object {
        fun fromPersons(persons: Collection<CensusPerson>): CensusHouseholdType {
            if(persons.size == 1) {
                return SINGLE
            }
            if(persons.numberOfAdults == 1 && persons.numberOfChildren > 0) {
                return SINGLE_PARENT
            }
            if(persons.numberOfAdults == 2) {
                if(persons.numberOfChildren > 0) {
                    return COUPLE_WITH_KIDS
                }
                else
                    return COUPLE_NO_KIDS
            }

            return OTHER_MULTICORE
        }
    }
}