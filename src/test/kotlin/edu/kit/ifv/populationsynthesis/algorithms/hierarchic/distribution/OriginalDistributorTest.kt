package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.algorithms.sampling.PickWithReplacement
import edu.kit.ifv.populationsynthesis.datastructures.Signature
import edu.kit.ifv.populationsynthesis.evaluation.Verification
import edu.kit.ifv.populationsynthesis.hierarchy.MutableHierarchyGraphFactory
import edu.kit.ifv.populationsynthesis.rules.LogicIndexer
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.measurement.NamedMeasurement
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.rules.provider.MapRuleProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.random.Random
import kotlin.test.Test

class OriginalDistributorTest {
    class Element(
        val first: Int,
        val second: Int,
    )
    @Test
    fun threeLevelHierarchyDistribution() {
        val hierarchy = MutableHierarchyGraphFactory.asForest {
            addRelationship(1, 2)
            addRelationship(2, 3)
            addRelationship(6, 4)
            addRelationship(5, 4)
            addRelationship(4, 3)
        }
        val logic: NamedMeasurement<Element> = NamedMeasurement.boolean("First Class") {
            it.first == 1
        }

        val logic2: NamedMeasurement<Element> = NamedMeasurement.boolean("Second Class") {
            it.second == 1
        }

        val elements = listOf(
            Element(1, 1),
            Element(0, 1),
            Element(1, 0),
        )
        val ruleProvider = MapRuleProvider<Int, Element>().apply {

            addRule(1, target = 1, logic = logic)
            addRule(5, 2, logic)
            addRule(6, 3, logic)
            addRule(2, 4, logic)
            addRule(4, 3, logic2)
            addRule(2, 0, logic2)
        }

        val logicIndexer: LogicIndexer<Int, Element> = LogicIndexer.fromProvider(ruleProvider)
        val householdMapping: Map<Signature, List<Element>>  = elements.groupBy { logicIndexer.createSignature(it)!!}
        val hierarchicRuleProvider: HierarchicRuleProvider<Int, Element> = ruleProvider.withHierarchy(hierarchy)
        val distributor = OriginalDistributor(
            hierarchicRuleProvider, logicIndexer,
            householdMapping,
            PickWithReplacement(Random(42))
        )

        assertEquals(householdMapping.values.size, elements.size) {
            "I expected ${elements.size} different signatures based on the two logics"
        }

        val initialSolution: List<SignatureAmount>  = householdMapping.keys.map { SignatureAmount(it, 1) }
        val output = distributor.distribute(initialSolution = initialSolution, rootArea = 3)

        assertEquals(setOf(1, 5, 6), output.keys)

        assertDoesNotThrow {
            Verification.verify(hierarchicRuleProvider, output)
        }
    }

    fun <AREA, T> MapRuleProvider<AREA, T>.addRule(area: AREA, target: Number, logic: NamedMeasurement<T>) {
        this.addRule(area, Rule(target.toDouble(), logic))
    }

}