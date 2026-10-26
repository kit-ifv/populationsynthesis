package edu.kit.ifv.populationsynthesis.domain.rules

import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.input.rules.CensusSchoolFormData

class CensusSchoolFormRules(
    map: Map<ARSKey, CensusSchoolFormData>
): CensusDataset<CensusSchoolFormData, CensusHousehold>(map) {

}