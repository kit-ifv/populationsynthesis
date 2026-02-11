package datasource

import edu.kit.ifv.populationsynthesis.datasource.CensusHousehold
import edu.kit.ifv.populationsynthesis.measurements.HouseholdSizeDefinition
import kotlin.test.Test
import kotlin.test.assertEquals

class HouseholdSizeDefinitionTest {

    @Test
    fun equalsEvaluation() {
        val measurement = HouseholdSizeDefinition(3, HouseholdSizeDefinition.EqualityOp.EQUALS).createNamedMeasurement()
        assertEquals(measurement.measure(spawnHousehold(4)),0.0)
        assertEquals(measurement.measure(spawnHousehold(3)),1.0)
        assertEquals(measurement.measure(spawnHousehold(2)),0.0)
        assertEquals(measurement.measure(spawnHousehold(1)),0.0)
    }
    @Test
    fun greaterEqualsEvaluation() {
        val measurement = HouseholdSizeDefinition(3, HouseholdSizeDefinition.EqualityOp.GREATER_OR_EQUAL).createNamedMeasurement()
        assertEquals(measurement.measure(spawnHousehold(4)),1.0)
        assertEquals(measurement.measure(spawnHousehold(3)),1.0)
        assertEquals(measurement.measure(spawnHousehold(2)),0.0)
        assertEquals(measurement.measure(spawnHousehold(1)),0.0)
    }
    private fun spawnHousehold(targetSize: Int) = CensusHousehold.fixedSize(targetSize)
}