package edu.kit.ifv.populationsynthesis.domain.rules

import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.input.rules.CensusDemographyData
import edu.kit.ifv.populationsynthesis.input.parseResource
import edu.kit.ifv.populationsynthesis.rules.HHAgeRuleFactory
import edu.kit.ifv.populationsynthesis.rules.HHSexRuleFactory

/**
 * Has 5 different rule Providers that can be built from census data set found at [TODO add file]
 */
class CensusDemographyRules(
    map: Map<ARSKey, CensusDemographyData>
) : CensusDataset<CensusDemographyData, CensusHousehold>(map) {

    val ageProvider by lazy {
        buildExhaustiveProvider(HHAgeRuleFactory::buildAgeCoverage)
    }

    val sexProvider by lazy {
        buildExhaustiveProvider(HHSexRuleFactory::buildSexCoverage)
    }
    companion object {
        private const val DEFAULT_RESOURCE = "census/Regionaltabelle_Demografie.csv"


        fun fromResource(resourceName: String = DEFAULT_RESOURCE): CensusDemographyRules {
            val output = parseResource<CensusDemographyData>(CensusDemographyRules::class.java, resourceName)
            return CensusDemographyRules(output.associateBy { it.arsKey })
        }
    }
}