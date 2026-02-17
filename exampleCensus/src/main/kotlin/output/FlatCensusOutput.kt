package edu.kit.ifv.populationsynthesis.output

import com.fasterxml.jackson.annotation.JsonUnwrapped
import edu.kit.ifv.populationsynthesis.domain.population.CensusPerson
import edu.kit.ifv.populationsynthesis.domain.population.HouseholdID

data class FlatCensusOutput(
    val area: String,
    val householdID: HouseholdID,
    @field:JsonUnwrapped
    val censusPerson: CensusPerson,
)