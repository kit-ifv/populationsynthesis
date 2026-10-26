package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.domain.population.Sex
import edu.kit.ifv.populationsynthesis.input.rules.CensusHighestSchoolDegreeData
import edu.kit.ifv.populationsynthesis.input.rules.SchoolDegree
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.covered.ExplicitTargetCoverageGroup
import edu.kit.ifv.populationsynthesis.rules.measurement.NumericMeasurementDefinition

object HHSchoolDegreeRuleFactory {
    fun build(input: CensusHighestSchoolDegreeData): CoverageGroup<CensusHousehold> {

        val ruleSet = Sex.values.flatMap { sex ->
            SchoolDegree.entries.mapNotNull { target ->
                optionalGenderedSchoolDegreeRule(sex, target, input)
            }
        }.toRuleSet()
        if(ruleSet.isEmpty()) return ExplicitTargetCoverageGroup(ruleSet, 0)
        return ExplicitTargetCoverageGroup(rules = ruleSet, target = input.SCHULABS_STP__2!!)
    }

    private fun optionalGenderedSchoolDegreeRule(sex: Sex, targetSchoolDegree: SchoolDegree, input: CensusHighestSchoolDegreeData): Rule<CensusHousehold>? {
        return GenderedSchoolDegreeDefinition(sex, targetSchoolDegree).makeOptionalRule(input.degreeWithSex(targetSchoolDegree, sex))
    }
}

class GenderedSchoolDegreeDefinition(
    val sex: Sex, val targetSchoolDegree: SchoolDegree
): NumericMeasurementDefinition<CensusHousehold>() {
    override fun evaluation(element: CensusHousehold): Number {
        return element.members.count {
            it.sex == this.sex && it.graduationCode in targetSchoolDegree.acceptedCodes
        }
    }

    override fun generateDescription(): String {
        return "Household School Degree $targetSchoolDegree sex == $sex"
    }
}