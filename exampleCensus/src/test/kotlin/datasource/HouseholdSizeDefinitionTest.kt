package datasource

import edu.kit.ifv.populationsynthesis.domain.population.Sex
import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.domain.population.CensusPerson
import edu.kit.ifv.populationsynthesis.rules.measurements.HouseholdSizeDefinition
import kotlin.random.Random
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
    private fun spawnHousehold(targetSize: Int) = fixedSizeHousehold(targetSize)
}

fun fixedSizeHousehold(size: Int, seed: Random = Random(1)): CensusHousehold {
    val members = (0 until size).map {
        randomCensusPerson(seed)
    }
    return CensusHousehold(members = members)
}

fun randomCensusPerson(seed: Random = Random(1)): CensusPerson {
    return CensusPerson(age = (0..100).random(seed), sex = Sex.random(seed))
}