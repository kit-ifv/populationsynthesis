package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import edu.kit.ifv.populationsynthesis.rules.LogicIndexer
import examples.threelayerscenario.A
import examples.threelayerscenario.ABCGraph
import examples.threelayerscenario.ABCRuleProvider
import examples.threelayerscenario.SeedElement
import kotlin.test.Test
import kotlin.test.assertEquals

class NakedIPUTest {
    /**
     * Since IPU without equivalence class may have duplicates, it should have duplicates.
     */
    @Test
    fun properDestructuringOfDuplicates() {
        val seedHouseholds = SeedElement.withDuplicates
        val ruleProvider = ABCRuleProvider.withHierarchy(ABCGraph)
        val ipu = NakedIPU(ruleProvider, seedHouseholds)
        val vectors = ipu.spawnVectorsFrom(LogicIndexer.fromProvider(ruleProvider).getLogic(A.A1))
        assertEquals(9, vectors.size)
        assertEquals(vectors.first(), vectors.last())
    }

    @Test
    fun duplication() {
        val seedHouseholds = SeedElement.withDuplicates
        val ruleProvider = ABCRuleProvider.withHierarchy(ABCGraph)
        val ipu = EquivalenceClassIPU(ruleProvider, seedHouseholds)
        val vectors = ipu.spawnVectorsFrom(LogicIndexer.fromProvider(ruleProvider).getLogics())
        assertEquals(8, vectors.size)
    }

}