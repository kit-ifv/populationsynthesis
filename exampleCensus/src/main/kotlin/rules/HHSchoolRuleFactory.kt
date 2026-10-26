package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.domain.population.Sex
import edu.kit.ifv.populationsynthesis.input.rules.CensusSchoolLevelData
import edu.kit.ifv.populationsynthesis.input.rules.SchoolLevel
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.covered.ExplicitTargetCoverageGroup
import edu.kit.ifv.populationsynthesis.rules.measurements.GenderedSchoolLevelDefinition

object HHSchoolRuleFactory {
    fun builgenderedSchoolCoverage(input: CensusSchoolLevelData): CoverageGroup<CensusHousehold> {
        val ruleset= Sex.values.flatMap { sex ->
            SchoolLevel.SPECIFIC.mapNotNull {  schoolLevel ->
                optionalSchoolRule(schoolLevel, sex, input)
            }
        }.toRuleSet()
        if(ruleset.isEmpty()) return ExplicitTargetCoverageGroup(ruleset, 0)
        return ExplicitTargetCoverageGroup(ruleset, target = requireNotNull(input.SCHUELER_KLSS_STP))
    }

    private fun optionalSchoolRule(schoolLevel: SchoolLevel, sex: Sex, input: CensusSchoolLevelData): Rule<CensusHousehold>? {
        return getSchoolDefinition(schoolLevel, sex).makeOptionalRule(input.getSchoolTarget(schoolLevel, sex))
    }

    fun getSchoolDefinition(schoolLevel: SchoolLevel, sex: Sex) = GenderedSchoolLevelDefinition(schoolLevel, sex)
}