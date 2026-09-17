package edu.kit.ifv.populationsynthesis.domain.rules

import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.input.parseResource
import edu.kit.ifv.populationsynthesis.input.rules.CensusHouseholdData
import edu.kit.ifv.populationsynthesis.input.rules.CensusSchoolLevelData
import edu.kit.ifv.populationsynthesis.rules.HHSchoolRuleFactory

class CensusSchoolLevelRules(
map: Map<ARSKey, CensusSchoolLevelData>
) : CensusDataset<CensusSchoolLevelData, CensusHousehold>(map) {

    val genderedSchoolProvider by lazy {
        buildExhaustiveProvider(HHSchoolRuleFactory::builgenderedSchoolCoverage)
    }
    companion object {

        private const val DEFAULT_RESOURCE = "census/Regionaltabelle_Klassenstufe.csv"

        fun fromResource(resourceName: String = DEFAULT_RESOURCE): CensusSchoolLevelRules {
            val output = parseResource<CensusSchoolLevelData>(CensusHouseholdRules::class.java, resourceName)
            return CensusSchoolLevelRules(output.associateBy { it.arsKey })
        }
    }
}