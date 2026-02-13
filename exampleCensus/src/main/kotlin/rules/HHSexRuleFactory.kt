package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.input.rules.CensusDemographyData
import edu.kit.ifv.populationsynthesis.domain.population.Sex
import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.covered.ExplicitTargetCoverageGroup
import edu.kit.ifv.populationsynthesis.rules.measurements.PersonSexDefinition
import edu.kit.ifv.populationsynthesis.rules.measurements.asHouseholdDefinition

object HHSexRuleFactory {

    fun buildSexCoverage(input: CensusDemographyData): CoverageGroup<CensusHousehold> {
        val ruleSet = Sex.values.mapNotNull {optionalSexRule(it, input)}.toRuleSet()
        return ExplicitTargetCoverageGroup(rules = ruleSet, target = requireNotNull(input.Insgesamt_))

    }

    private fun optionalSexRule(sex: Sex, input: CensusDemographyData): Rule<CensusHousehold>? {
        return PersonSexDefinition(sex).asHouseholdDefinition().makeOptionalRule(input.getSexTarget(sex))
    }
}
