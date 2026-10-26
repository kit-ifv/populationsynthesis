package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import edu.kit.ifv.populationsynthesis.rules.provider.MapRuleProvider
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.Area
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.A
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.ABCGraph
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.B
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.C
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.HelpGenerator
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.SeedElement
import kotlin.test.Test

class HistoricIPUTest {

    @Test
    fun differentRuleBranches() {
        val ruleProvider = MapRuleProvider<Area, SeedElement>().apply {
            addRules(C.C1, HelpGenerator.C(16, 16))
            addRules(B.B1, HelpGenerator.B(12, 12))
            addRules(B.B2, HelpGenerator.B(4, 4))
            addRules(A.A1, HelpGenerator.A(5, 5))
            addRules(A.A2, HelpGenerator.A(7, 7))
            addRules(A.A3, HelpGenerator.A(1, 2))
            addRules(A.A4, HelpGenerator.A(4, 3))
        }
        val hierarchicProvider = ruleProvider.withHierarchy(ABCGraph)
        val composedRules = hierarchicProvider.getComposedRules(B.B2)

        val testElement = SeedElement(1)
        composedRules.forEach { it.measure(testElement) }
        composedRules.forEach { it.measure(Inheritor()) }
    }
    private class Inheritor(): SeedElement(2)
}