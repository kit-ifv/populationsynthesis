package measurements

import edu.kit.ifv.populationsynthesis.datasource.CensusHousehold
import edu.kit.ifv.populationsynthesis.datasource.CensusPerson
import edu.kit.ifv.populationsynthesis.measurements.PersonAgeDefinition
import edu.kit.ifv.populationsynthesis.measurements.asHouseholdDefinition
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class PersonAgeDefinitionTest {

    @Test
    fun properMeasures() {
        val test = PersonAgeDefinition(0..5)
        assertEquals(test.evaluation(CensusPerson(age = 4)), true)
        assertEquals(test.evaluation(CensusPerson(age = 6)), false)
        assertEquals(test.evaluation(CensusPerson(age = -1)), false)
    }

    @Test
    fun asHouseholdDefinition() {

        val test = PersonAgeDefinition(0..5).asHouseholdDefinition()

        assertEquals(test.createNamedMeasurement().measure(CensusHousehold(1, 1)), 2.0)
        assertEquals(test.createNamedMeasurement().measure(CensusHousehold(5)), 1.0)
    }

}