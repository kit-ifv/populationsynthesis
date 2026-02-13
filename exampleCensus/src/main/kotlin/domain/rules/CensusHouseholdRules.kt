package edu.kit.ifv.populationsynthesis.domain.rules

import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.input.rules.CensusHouseholdData
import edu.kit.ifv.populationsynthesis.input.parseResource
import edu.kit.ifv.populationsynthesis.rules.HHSizeRuleFactory
import edu.kit.ifv.populationsynthesis.rules.provider.ExhaustiveRuleProvider

class CensusHouseholdRules(
    map: Map<ARSKey, CensusHouseholdData>
) : CensusDataset<CensusHouseholdData, CensusHousehold>(map) {
    val sizeProvider by lazy {
        buildExhaustiveProvider(HHSizeRuleFactory::buildFor5)
    }

    val size6plusProvider by lazy {
        buildExhaustiveProvider(HHSizeRuleFactory::buildFor6)
    }




    val familienTypProvider: ExhaustiveRuleProvider<ARSKey, CensusHousehold> by lazy {
        TODO()
    }

    val lebenstypProvider: ExhaustiveRuleProvider<ARSKey, CensusHousehold> by lazy {
        TODO()

    }

    val seniorenTypProvider:  ExhaustiveRuleProvider<ARSKey, CensusHousehold> by lazy {
        TODO()

    }

    companion object {

        private const val DEFAULT_RESOURCE = "census/Regionaltabelle_Haushalte.csv"

        fun fromResource(resourceName: String = DEFAULT_RESOURCE): CensusHouseholdRules {
            val output = parseResource<CensusHouseholdData>(CensusHouseholdRules::class.java, resourceName)
            return CensusHouseholdRules(output.associateBy { it.arsKey })
        }
    }
}