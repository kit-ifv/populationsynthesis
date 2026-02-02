package examples.layerscenario

import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu.EquivalenceClassIPU
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu.HistoricIPU
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu.NakedIPU
import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.fail


private typealias IPUSpawner = (KonduriRuleProvider, GenericIPU) -> HistoricIPU<KonduriArea, KonduriHousehold>

class KonduriTests {

    private val contributions =
        RTypeSet.generateContributions() + HTypeSet.generateContributions() + PTypeSet.generateContributions()

    companion object {
        @JvmStatic
        fun ipuFactories(): List<Arguments> = listOf(
            Arguments.of(
                "NakedIPU", { rp: KonduriRuleProvider, iters: GenericIPU -> NakedIPU(rp, KonduriHousehold.all, iters) }
            ),
            Arguments.of(
                "EquivalenceIPU",
                { rp: KonduriRuleProvider, iters: GenericIPU -> EquivalenceClassIPU(rp, KonduriHousehold.all, iters) }
            )

        )

    }


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

    @ParameterizedTest(name = "{0}")
    @MethodSource("ipuFactories")
    fun testLegacyIPU(
        name: String,
        spawner: IPUSpawner
    ) {
        val ruleProvider = KonduriRuleProvider()
        val iterationAtRegionLevel = GenericIPU { _, observers ->
            observers.take(3).forEach { it.optimize() }

        }
        val ipu = spawner.invoke(
            ruleProvider,
            iterationAtRegionLevel
        )
        val outp = ipu.calculate(KonduriRegion, listOf(KonduriGeographicUnit.geo1, KonduriGeographicUnit.geo2))

        val vectorsGeo1 = outp[KonduriGeographicUnit.geo1]?.toList() ?: emptyList()
        val expectedScalars = vectorsGeo1
        assertEquals(expectedScalars[0].scalar, 13 + 2.0 / 3)
        assertEquals(expectedScalars[1].scalar, 21 + 1.0 / 2)
        assertEquals(expectedScalars[2].scalar, 10 + 1.0 / 6)
        assertEquals(expectedScalars[3].scalar, 21 + 1.0 / 2)
        assertEquals(expectedScalars[4].scalar, 10 + 1.0 / 6)
        assertEquals(expectedScalars[5].scalar, 13 + 2.0 / 3)
        assertEquals(expectedScalars[6].scalar, 10 + 1.0 / 6)
        assertEquals(expectedScalars[7].scalar, 13 + 2.0 / 3)

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ipuFactories")
    fun afterControllingGeoIteration1(name: String, spawner: IPUSpawner) {
        val ruleProvider = KonduriRuleProvider()
        val iterationAtRegionLevel = GenericIPU { _, observers ->
            observers.take(4).forEach { it.optimize() }

        }
        val ipu = spawner.invoke(
            ruleProvider,
            iterationAtRegionLevel
        )
        val output = ipu.calculate(KonduriRegion, listOf(KonduriGeographicUnit.geo1, KonduriGeographicUnit.geo2))

        val vectorsGeo1 = output[KonduriGeographicUnit.geo1]?.toList() ?: fail("The output for geo1 should be present")

        val expectedScalars = vectorsGeo1
        assertEquals(expectedScalars[0].scalar, 13.87, 0.01)
        assertEquals(expectedScalars[1].scalar, 21.82, 0.01)
        assertEquals(expectedScalars[2].scalar, 10.32, 0.01)
        assertEquals(expectedScalars[3].scalar, 21.5, 0.01)
        assertEquals(expectedScalars[4].scalar, 10.17, 0.01)
        assertEquals(expectedScalars[5].scalar, 13.67, 0.01)
        assertEquals(expectedScalars[6].scalar, 10.17, 0.01)
        assertEquals(expectedScalars[7].scalar, 13.67, 0.01)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ipuFactories")
    fun afterFull1000Iterations(name: String, spawner: IPUSpawner) {
        val ruleProvider = KonduriRuleProvider()
        val iterationAtRegionLevel = GenericIPU { _, observers ->
            repeat(1000) {

                observers.forEach { it.optimize() }
            }
        }
        val ipu = spawner(
            ruleProvider,
            iterationAtRegionLevel
        )
        val output = ipu.calculate(KonduriRegion, listOf(KonduriGeographicUnit.geo1, KonduriGeographicUnit.geo2))

        val vectorsGeo1 = output[KonduriGeographicUnit.geo1]?.toList() ?: fail("The output for geo1 should be present")

        val expectedScalars = vectorsGeo1

        assertEquals(expectedScalars[0].scalar, 8.33, 0.01)
        assertEquals(expectedScalars[1].scalar, 25.71, 0.01)
        assertEquals(expectedScalars[2].scalar, 12.19, 0.01)
        assertEquals(expectedScalars[3].scalar, 12.19, 0.01)
        assertEquals(expectedScalars[4].scalar, 20.02, 0.01)
        assertEquals(expectedScalars[5].scalar, 8.22, 0.01)
        assertEquals(expectedScalars[6].scalar, 2.78, 0.01)
        assertEquals(expectedScalars[7].scalar, 8.22, 0.01)


        val vectorsGeo2 = output[KonduriGeographicUnit.geo2]?.toList() ?: fail("The output for geo2 should be present")

        val expectedScalars2 = vectorsGeo2
        assertEquals(expectedScalars2[0].scalar, 4.46, 0.01)
        assertEquals(expectedScalars2[1].scalar, 17.71, 0.01)
        assertEquals(expectedScalars2[2].scalar, 11.00, 0.01)
        assertEquals(expectedScalars2[3].scalar, 30.39, 0.01)
        assertEquals(expectedScalars2[4].scalar, 10.31, 0.01)
        assertEquals(expectedScalars2[5].scalar, 26.85, 0.01)
        assertEquals(expectedScalars2[6].scalar, 5.38, 0.01)
        assertEquals(expectedScalars2[7].scalar, 26.85, 0.01)
    }
}
