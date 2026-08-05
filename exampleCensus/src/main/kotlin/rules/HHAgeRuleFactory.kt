package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.domain.population.AgeGroupCode
import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.input.rules.CensusDemographyData
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.covered.ExplicitTargetCoverageGroup
import edu.kit.ifv.populationsynthesis.rules.measurements.PersonAgeDefinition
import edu.kit.ifv.populationsynthesis.rules.measurements.asHouseholdDefinition

object HHAgeRuleFactory {


    fun buildAgeCoverage(input: CensusDemographyData): CoverageGroup<CensusHousehold> {
        val ruleSet = AgeGroupCode.ALL.mapNotNull { optionalAgeRule(it, input) }.toRuleSet()

        return ExplicitTargetCoverageGroup(rules = ruleSet, target = requireNotNull(input.Insgesamt_))
    }


    fun getAgeDefinition(ageCode: AgeGroupCode) = PersonAgeDefinition(ageCode)

    private fun optionalAgeRule(ageCode: AgeGroupCode, input: CensusDemographyData): Rule<CensusHousehold>? {
        return getAgeDefinition(ageCode).asHouseholdDefinition().makeOptionalRule(input.getAgeTarget(ageCode))
    }
}
