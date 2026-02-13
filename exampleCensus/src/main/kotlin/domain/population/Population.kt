package edu.kit.ifv.populationsynthesis.domain.population

import edu.kit.ifv.populationsynthesis.input.population.PersonInfo

class Population(val households: List<CensusHousehold>) {


    companion object {
        fun fromPersonInfo(infos: Collection<PersonInfo> = PersonInfo.fromResource()): Population {
            val groupedInfos = infos.groupBy { it.householdID }
            val households = groupedInfos.map { (householdID, personInfos) ->
                CensusHousehold(
                    id = householdID,
                    members = personInfos.map { it.toCensusPerson() }
                )
            }
            return Population(households)
        }
    }
}