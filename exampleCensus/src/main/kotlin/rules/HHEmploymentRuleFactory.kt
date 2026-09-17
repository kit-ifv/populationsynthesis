package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.domain.population.Sex
import edu.kit.ifv.populationsynthesis.input.population.Employment
import edu.kit.ifv.populationsynthesis.input.rules.CensusEmploymentStatusData
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.covered.ExplicitTargetCoverageGroup
import edu.kit.ifv.populationsynthesis.rules.measurement.NumericMeasurementDefinition

object HHEmploymentRuleFactory {

    fun build(input: CensusEmploymentStatusData): CoverageGroup<CensusHousehold> {

        val ruleSet = Sex.values.flatMap { sex ->
            EmploymentStatus.SENSIBLE_TARGETS.mapNotNull { target ->
                optionalGenderedEmploymentRule(sex, target, input)
            }
        }.toRuleSet()
        if(ruleSet.isEmpty()) return ExplicitTargetCoverageGroup(ruleSet, 0)
        return ExplicitTargetCoverageGroup(rules = ruleSet, target = input.ERWERBSTAT_KURZ_STP!!)
    }

    private fun optionalGenderedEmploymentRule(sex: Sex, targetEmploymentStatus: EmploymentStatus, input: CensusEmploymentStatusData): Rule<CensusHousehold>? {
        return GenderedEmploymentStatusDefinition(sex, targetEmploymentStatus).makeOptionalRule(input.getGenderedEmploymentStatus(sex, targetEmploymentStatus))
    }

}

class GenderedEmploymentStatusDefinition(val sex: Sex, val targetEmploymentStatus: EmploymentStatus): NumericMeasurementDefinition<CensusHousehold>() {
    override fun evaluation(element: CensusHousehold): Int {
        return element.members.count { it.sex == this.sex && it.employment in targetEmploymentStatus.acceptedEmployments }
    }

    override fun generateDescription(): String {
        return "Household Employment Status == $targetEmploymentStatus && sex == $sex"
    }
}


enum class EmploymentStatus(val acceptedEmployments: Set<Employment>) {
    ANY(Employment.entries.toSet() - Employment.UNKNOWN),
    EMPLOYED(setOf(Employment.FULLTIME, Employment.PARTTIME, Employment.MARGINAL, Employment.EDUCATION)),
    UNEMPLOYED(setOf(Employment.UNEMPLOYED)),
    NOT_WORKING(
        Employment.entries.toSet() - setOf(
            Employment.FULLTIME,
            Employment.PARTTIME,
            Employment.MARGINAL,
            Employment.EDUCATION,
            Employment.UNEMPLOYED
        )
    );


    companion object {
        val SENSIBLE_TARGETS = entries - EmploymentStatus.ANY
    }


}