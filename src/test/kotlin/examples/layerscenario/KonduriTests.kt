package examples.layerscenario

import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu.NakedIPU
import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class KonduriTests {

    private val contributions =
        RTypeSet.generateContributions() + HTypeSet.generateContributions() + PTypeSet.generateContributions()

    @Test
    fun conversion() {
        test(KonduriHousehold.first, 0, 0, 1, 1, 0, 1, 1, 1)
        test(KonduriHousehold.second, 1, 0, 0, 1, 0, 1, 0, 1)
        test(KonduriHousehold.third, 0, 1, 0, 1, 0, 2, 1, 0)
        test(KonduriHousehold.fourth, 1, 0, 0, 0, 1, 1, 0, 2)
        test(KonduriHousehold.fifth, 0, 1, 0, 0, 1, 0, 2, 1)
        test(KonduriHousehold.sixth, 0, 0, 1, 0, 1, 1, 1, 0)
        test(KonduriHousehold.seventh, 0, 1, 0, 0, 1, 2, 1, 2)
        test(KonduriHousehold.eighth, 0, 0, 1, 0, 1, 1, 2, 0)


    }

    private fun test(konduriHousehold: KonduriHousehold, vararg expected: Int) {
        assertContentEquals(
            ScalableVector.createFrom(konduriHousehold, contributions).content,
            expected.map { it.toDouble() })
    }

    @Test
    fun testLegacyIPU() {
        val ruleProvider = KonduriRuleProvider()
        val iterationAtRegionLevel = GenericIPU { _, observers ->
            observers.take(3).forEach { it.optimize() }

        }
        val ipu = NakedIPU(
            ruleProvider,
            KonduriHousehold.all,
            iterationAtRegionLevel
        )
        val outp = ipu.calculate(KonduriRegion, listOf(KonduriGeographicUnit.geo1, KonduriGeographicUnit.geo2))

        val vectorsGeo1 = outp[KonduriGeographicUnit.geo1]?.entries ?: emptySet()

        val expectedScalars = vectorsGeo1.associate { it.value.first() to it.key}
        assertEquals(expectedScalars.getValue(KonduriHousehold.first).scalar, 13 + 2.0 /3 )
        assertEquals(expectedScalars.getValue(KonduriHousehold[2]).scalar, 21 + 1.0 /2 )
        assertEquals(expectedScalars.getValue(KonduriHousehold[3]).scalar, 10 + 1.0 /6 )
        assertEquals(expectedScalars.getValue(KonduriHousehold[4]).scalar, 21 + 1.0 /2  )
        assertEquals(expectedScalars.getValue(KonduriHousehold[5]).scalar, 10 + 1.0 /6 )
        assertEquals(expectedScalars.getValue(KonduriHousehold[6]).scalar, 13 + 2.0 /3 )
        assertEquals(expectedScalars.getValue(KonduriHousehold[7]).scalar, 10 + 1.0 /6 )
        assertEquals(expectedScalars.getValue(KonduriHousehold[8]).scalar, 13 + 2.0 /3 )

    }

}