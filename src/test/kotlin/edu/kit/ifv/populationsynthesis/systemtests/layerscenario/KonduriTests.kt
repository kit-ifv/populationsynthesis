package edu.kit.ifv.populationsynthesis.systemtests.layerscenario

import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu.EquivalenceClassIPU
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu.HistoricIPU
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu.NakedIPU
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.MatrixSolver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.toList
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.fail


private typealias IPUSpawner = (KonduriRuleProvider, MatrixSolver) -> HistoricIPU<KonduriArea, KonduriHousehold>

class KonduriTests {

    private val measurements =
        RTypeSet.generateMeasurements() + HTypeSet.generateMeasurements() + PTypeSet.generateMeasurements()

    companion object {
        @JvmStatic
        fun ipuFactories(): List<Arguments> = listOf(
            Arguments.of(
                "NakedIPU", { rp: KonduriRuleProvider, iters: MatrixSolver -> NakedIPU(rp, KonduriHousehold.all, iters) }
            ),
            Arguments.of(
                "EquivalenceIPU",
                { rp: KonduriRuleProvider, iters: MatrixSolver -> EquivalenceClassIPU(rp, KonduriHousehold.all, iters) }
            )

        )

    }


    @Test
    fun conversion() {
        test(KonduriHousehold.first, 0, 0, 1, 1, 0, 1, 1, 1)
        test(KonduriHousehold.second, 1, 0, 0, 1, 0, 1, 0, 1)
        test(KonduriHousehold.third, 0, 1, 0, 1, 0, 2, 1)
        test(KonduriHousehold.fourth, 1, 0, 0, 0, 1, 1, 0, 2)
        test(KonduriHousehold.fifth, 0, 1, 0, 0, 1, 0, 2, 1)
        test(KonduriHousehold.sixth, 0, 0, 1, 0, 1, 1, 1)
        test(KonduriHousehold.seventh, 0, 1, 0, 0, 1, 2, 1, 2)
        test(KonduriHousehold.eighth, 0, 0, 1, 0, 1, 1, 2)


    }

    private fun test(konduriHousehold: KonduriHousehold, vararg expected: Int) {
        assertContentEquals(
            ScalableVector.createFromLogics(konduriHousehold, measurements).toList(),
            expected.map { it.toDouble() })
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ipuFactories")
    fun testLegacyIPU(
        name: String,
        spawner: IPUSpawner
    ) {
        val ruleProvider = KonduriRuleProvider()
        val iterationAtRegionLevel = MatrixSolver { _, observers ->
            observers.take(3).forEach { it.optimize() }

        }
        val ipu = spawner.invoke(
            ruleProvider,
            iterationAtRegionLevel
        )
        val outp = ipu.calculate(KonduriRegion, listOf(KonduriGeographicUnit.geo1, KonduriGeographicUnit.geo2))

        val vectorsGeo1 = outp[KonduriGeographicUnit.geo1]?.toList() ?: emptyList()
        assertEquals(13 + 2.0 / 3, vectorsGeo1[0].scalar)
        assertEquals(21 + 1.0 / 2, vectorsGeo1[1].scalar)
        assertEquals(10 + 1.0 / 6, vectorsGeo1[2].scalar)
        assertEquals(21 + 1.0 / 2, vectorsGeo1[3].scalar)
        assertEquals(10 + 1.0 / 6, vectorsGeo1[4].scalar)
        assertEquals(13 + 2.0 / 3, vectorsGeo1[5].scalar)
        assertEquals(10 + 1.0 / 6, vectorsGeo1[6].scalar)
        assertEquals(13 + 2.0 / 3, vectorsGeo1[7].scalar)

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ipuFactories")
    fun afterControllingGeoIteration1(name: String, spawner: IPUSpawner) {
        val ruleProvider = KonduriRuleProvider()
        val iterationAtRegionLevel = MatrixSolver { _, observers ->
            observers.take(4).forEach { it.optimize() }

        }
        val ipu = spawner.invoke(
            ruleProvider,
            iterationAtRegionLevel
        )
        val output = ipu.calculate(KonduriRegion, listOf(KonduriGeographicUnit.geo1, KonduriGeographicUnit.geo2))

        val expectedScalars = output[KonduriGeographicUnit.geo1]?.toList() ?: fail("The output for geo1 should be present")

        assertEquals(13.87, expectedScalars[0].scalar, 0.01)
        assertEquals(21.82, expectedScalars[1].scalar, 0.01)
        assertEquals(10.32, expectedScalars[2].scalar, 0.01)
        assertEquals(21.5, expectedScalars[3].scalar, 0.01)
        assertEquals(10.17, expectedScalars[4].scalar, 0.01)
        assertEquals(13.67, expectedScalars[5].scalar, 0.01)
        assertEquals(10.17, expectedScalars[6].scalar, 0.01)
        assertEquals(13.67, expectedScalars[7].scalar, 0.01)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ipuFactories")
    fun afterFull1000Iterations(name: String, spawner: IPUSpawner) {
        val ruleProvider = KonduriRuleProvider()
        val iterationAtRegionLevel = MatrixSolver { _, observers ->
            repeat(1000) {

                observers.forEach { it.optimize() }
            }
        }
        val ipu = spawner(
            ruleProvider,
            iterationAtRegionLevel
        )
        val output = ipu.calculate(KonduriRegion, listOf(KonduriGeographicUnit.geo1, KonduriGeographicUnit.geo2))

        val expectedScalarsArea1 = output[KonduriGeographicUnit.geo1]?.toList() ?: fail("The output for geo1 should be present")

        assertEquals(8.33, expectedScalarsArea1[0].scalar, 0.01)
        assertEquals(25.71, expectedScalarsArea1[1].scalar, 0.01)
        assertEquals(12.19, expectedScalarsArea1[2].scalar, 0.01)
        assertEquals(12.19, expectedScalarsArea1[3].scalar, 0.01)
        assertEquals(20.02, expectedScalarsArea1[4].scalar, 0.01)
        assertEquals(8.22, expectedScalarsArea1[5].scalar, 0.01)
        assertEquals(2.78, expectedScalarsArea1[6].scalar, 0.01)
        assertEquals(8.22, expectedScalarsArea1[7].scalar, 0.01)


        val expectedScalarsArea2 = output[KonduriGeographicUnit.geo2]?.toList() ?: fail("The output for geo2 should be present")

        assertEquals(4.46, expectedScalarsArea2[0].scalar, 0.01)
        assertEquals(17.71, expectedScalarsArea2[1].scalar, 0.01)
        assertEquals(11.00, expectedScalarsArea2[2].scalar, 0.01)
        assertEquals(30.39, expectedScalarsArea2[3].scalar, 0.01)
        assertEquals(10.31, expectedScalarsArea2[4].scalar, 0.01)
        assertEquals(26.85, expectedScalarsArea2[5].scalar, 0.01)
        assertEquals(5.38, expectedScalarsArea2[6].scalar, 0.01)
        assertEquals(26.85, expectedScalarsArea2[7].scalar, 0.01)
    }
}
