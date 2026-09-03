package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import edu.kit.ifv.populationsynthesis.systemtests.layerscenario.KonduriGeographicUnit
import edu.kit.ifv.populationsynthesis.systemtests.layerscenario.KonduriHousehold
import edu.kit.ifv.populationsynthesis.systemtests.layerscenario.KonduriRuleProvider
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.A
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.ABCGraph
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.ABCRuleProvider
import edu.kit.ifv.populationsynthesis.systemtests.threelayerscenario.SeedElement
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

class RuleObserverBuilderTest {

    @Test
    fun properObserverCreation() {
        val builder = RuleObserverBuilder(KonduriRuleProvider())
        val output = builder.build(KonduriGeographicUnit.geo1, KonduriHousehold.all)
        output.forEach { assertTrue(it.sanityCheck()) }
    }

    @Test
    fun equivalenceClassAsInput() {
        val builder = RuleObserverBuilder(ABCRuleProvider.withHierarchy(ABCGraph))
        val elements = SeedElement.withDuplicates
        val output = builder.build(A.A1, elements)
        output.forEach { assertTrue(it.sanityCheck()) }
    }

}