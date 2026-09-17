package edu.kit.ifv.populationsynthesis.rules.measurements

import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.domain.population.CensusPerson
import edu.kit.ifv.populationsynthesis.domain.population.Sex
import edu.kit.ifv.populationsynthesis.input.rules.SchoolLevel
import edu.kit.ifv.populationsynthesis.rules.measurement.NumericMeasurementDefinition

class GenderedSchoolLevelDefinition(val schoolLevel: SchoolLevel, val sex: Sex): NumericMeasurementDefinition<CensusHousehold>() {
    override fun evaluation(element: CensusHousehold): Int {
        return element.members.count { it.sex == sex && SchoolLevelDeterminer.determine(it) == schoolLevel }
    }

    override fun generateDescription(): String {
        return "Household School Level $schoolLevel && Sex == $sex Count"
    }
}

object SchoolLevelDeterminer {
    fun determine(person: CensusPerson): SchoolLevel? {
        if(!person.employment.isStudent()) return null
        return SchoolLevel.SPECIFIC.firstOrNull{person.age in it.ageRange} ?: SchoolLevel.TOTAL
    }
}