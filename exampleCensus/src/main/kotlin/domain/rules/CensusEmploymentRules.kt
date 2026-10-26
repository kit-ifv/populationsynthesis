package edu.kit.ifv.populationsynthesis.domain.rules

import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.input.parseResource
import edu.kit.ifv.populationsynthesis.input.rules.CensusEmploymentStatusData
import edu.kit.ifv.populationsynthesis.input.rules.CensusSchoolLevelData
import edu.kit.ifv.populationsynthesis.rules.HHEmploymentRuleFactory

class CensusEmploymentRules(
    map: Map<ARSKey, CensusEmploymentStatusData>
) : CensusDataset<CensusEmploymentStatusData, CensusHousehold>(map) {


    val genderedEmploymentProvider by lazy {
        buildExhaustiveProvider(HHEmploymentRuleFactory::build)
    }

    companion object {

        private const val DEFAULT_RESOURCE = "census/Regionaltabelle_Erwerbsstatus.csv"

        fun fromResource(resourceName: String = DEFAULT_RESOURCE): CensusEmploymentRules {
            val output = parseResource<CensusEmploymentStatusData>(CensusEmploymentStatusData::class.java, resourceName)
            return CensusEmploymentRules(output.associateBy { it.arsKey })
        }
    }
}