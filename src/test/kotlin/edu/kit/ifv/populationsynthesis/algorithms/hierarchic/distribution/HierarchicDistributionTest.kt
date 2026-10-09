package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.MatrixSolver
import edu.kit.ifv.populationsynthesis.hierarchy.HierarchyGraphFactory
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.measurement.NamedMeasurement
import edu.kit.ifv.populationsynthesis.rules.provider.MapRuleProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class HierarchicDistributionTest {

    private data class Elements(val attr: Attr)
    private enum class Attr {
        A, B, C
    }

    @Test
    fun differentRegisteredRulesWork() {
        val ruleProvider = MapRuleProvider<Int, Elements>().apply {

            addRule(1, Rule(target = 10.0, logic = NamedMeasurement.boolean("ATest", logic = { it.attr == Attr.A })))
            addRule(1, Rule(target = 10.0, logic = NamedMeasurement.boolean("BTest", logic = { it.attr == Attr.B })))
            addRule(1, Rule(target = 10.0, logic = NamedMeasurement.boolean("CTest", logic = { it.attr == Attr.C })))
            addRule(2, Rule(target = 10.0, logic = NamedMeasurement.boolean("ATest", logic = { it.attr == Attr.A })))
        }
        val hierarchy = HierarchyGraphFactory.asForest<Int> {
            addVertex(1)
            addVertex(2)
        }
        val hierarchicRuleProvider = ruleProvider.withHierarchy(hierarchy)
        val ipu = HierarchicDistribution(
            ruleProvider = hierarchicRuleProvider,
            seedElements = listOf(Elements(Attr.A), Elements(Attr.B), Elements(Attr.C)),
            config = HierarchicDistributionConfig(

                matrixSolver = MatrixSolver.legacy,
            )
        )


        val output = ipu.synthesizeAll()


        assertEquals(output[1]!!.size, 30)
        assertEquals(output[2]!!.size, 10)
        assertFalse(output[2]!!.contains(Elements(Attr.B)))
        assertFalse(output[2]!!.contains(Elements(Attr.C)))

    }

}