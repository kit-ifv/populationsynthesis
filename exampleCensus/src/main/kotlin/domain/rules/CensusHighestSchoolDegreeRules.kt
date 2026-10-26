package edu.kit.ifv.populationsynthesis.domain.rules

import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.input.parseResource
import edu.kit.ifv.populationsynthesis.input.rules.CensusEmploymentStatusData
import edu.kit.ifv.populationsynthesis.input.rules.CensusHighestSchoolDegreeData
import edu.kit.ifv.populationsynthesis.rules.HHSchoolDegreeRuleFactory

class CensusHighestSchoolDegreeRules(
    map: Map<ARSKey, CensusHighestSchoolDegreeData>
) : CensusDataset<CensusHighestSchoolDegreeData, CensusHousehold>(map) {
    val genderedSchoolDegreeProvider by lazy {
        buildExhaustiveProvider(HHSchoolDegreeRuleFactory::build)
    }

    companion object {

        private const val DEFAULT_RESOURCE = "census/Regionaltabelle_HoechsterSchulabschluss.csv"

        fun fromResource(resourceName: String = DEFAULT_RESOURCE): CensusHighestSchoolDegreeRules {
            val output = parseResource<CensusHighestSchoolDegreeData>(CensusHighestSchoolDegreeData::class.java, resourceName)
            return CensusHighestSchoolDegreeRules(output.associateBy { it.arsKey })
        }
    }
}
