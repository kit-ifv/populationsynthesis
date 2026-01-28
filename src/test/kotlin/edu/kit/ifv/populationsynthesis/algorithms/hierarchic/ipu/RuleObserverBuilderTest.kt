package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import examples.layerscenario.KonduriGeographicUnit
import examples.layerscenario.KonduriHousehold
import examples.layerscenario.KonduriRuleProvider
import examples.threelayerscenario.A
import examples.threelayerscenario.ABCGraph
import examples.threelayerscenario.ABCRuleProvider
import examples.threelayerscenario.SeedElement
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