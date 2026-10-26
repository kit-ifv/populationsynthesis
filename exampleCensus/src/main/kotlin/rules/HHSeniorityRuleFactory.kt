package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.domain.population.SeniorityStatus
import edu.kit.ifv.populationsynthesis.input.rules.CensusHouseholdData
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.covered.ExplicitTargetCoverageGroup
import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition

object HHSeniorityRuleFactory {

    fun build(input: CensusHouseholdData): CoverageGroup<CensusHousehold> {
        val ruleSet = SeniorityStatus.entries.mapNotNull{optionalEqualityRule(it, input)}.toRuleSet()
        return ExplicitTargetCoverageGroup(rules = ruleSet, target = requireNotNull(input.Insgesamt_val))
    }

    private fun optionalEqualityRule(seniorityStatus: SeniorityStatus, input: CensusHouseholdData): Rule<CensusHousehold>? {
        return SeniorityStatusDefinition(seniorityStatus).makeOptionalRule(input.getSeniorityTarget(seniorityStatus))
    }
}

class SeniorityStatusDefinition(val seniorityStatus: SeniorityStatus): BooleanMeasurementDefinition<CensusHousehold>() {
    override fun evaluation(element: CensusHousehold): Boolean {
        return element.seniorityStatus == seniorityStatus
    }

    override fun generateDescription(): String {
        return "Household Seniority Status == $seniorityStatus"
    }
}