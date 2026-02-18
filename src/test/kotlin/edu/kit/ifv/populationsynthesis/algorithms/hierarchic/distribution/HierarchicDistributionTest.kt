package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU
import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.hierarchy.HierarchyGraphFactory
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.measurement.NamedMeasurement
import edu.kit.ifv.populationsynthesis.rules.provider.MapRuleProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HierarchicDistributionTest {

    private class Elements(val attr: Attr)
    private enum class Attr{
        A, B, C
    }
    @Test
    fun differentRegisteredRulesWork() {
        val ruleProvider = MapRuleProvider<Int, Elements>().apply {

            addRule(1, Rule(target = 10.0, logic = NamedMeasurement.boolean("ATest", logic = {it.attr == Attr.A})))
            addRule(1, Rule(target = 10.0, logic = NamedMeasurement.boolean("BTest", logic = {it.attr == Attr.B})))
            addRule(1, Rule(target = 10.0, logic = NamedMeasurement.boolean("CTest", logic = {it.attr == Attr.C})))
            addRule(2, Rule(target = 10.0, logic = NamedMeasurement.boolean("ATest", logic = {it.attr == Attr.A})))
        }
        val hierarchy = HierarchyGraphFactory.asForest<Int> {
            addVertex(1)
            addVertex(2)
        }
        val hierarchicRuleProvider = ruleProvider.withHierarchy(hierarchy)

        val ipu = HierarchicDistribution(
            ruleProvider = hierarchicRuleProvider,
            seedHouseholds = listOf(Elements(Attr.A), Elements(Attr.B), Elements(Attr.C)),
            config = HierarchicDistributionConfig(

                ipu = GenericIPU.legacy
            )
        )


        ipu.synthesizeAll()
    }

}