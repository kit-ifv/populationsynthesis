package edu.kit.ifv.populationsynthesis.output

import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold

object CensusHouseholdConverter  {
    fun convert(map: Map<ARSKey, Collection<CensusHousehold>>): List<FlatCensusOutput> {
        return map.entries.flatMap { (k, v) ->
            v.flatMap{hh ->
                hh.members.map { member ->
                    FlatCensusOutput(
                        k.toString(),
                        hh.id,
                        member
                    )
                }
            }
        }
    }
}

