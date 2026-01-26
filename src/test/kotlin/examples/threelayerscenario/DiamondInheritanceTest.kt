package examples.threelayerscenario

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchyGraphFactory
import edu.kit.ifv.populationsynthesis.rules.composer.HelperRules
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.rules.provider.MapRuleProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DiamondInheritanceTest {
    private val simpleDiamond = HierarchyGraphFactory.asDAG<Area>(guard = C.C2) {
        addRelationship(A.A1, B.B1)
        addRelationship(A.A1, B.B2)
        addRelationship(B.B1, C.C1)
        addRelationship(B.B2, C.C1)

    }

    private val diamondWithEmptyAreas = HierarchyGraphFactory.asDAG<Area>(guard = C.C2) {
        addRelationship(A.A1, B.B1)
        addRelationship(A.A1, B.B2)
        addRelationship(B.B1, C.C1)
        addRelationship(B.B2, C.C1)

        addRelationship(A.A2, B.B1)
        addRelationship(A.A3, B.B2)

    }

    @Test
    fun properRuleComposition() {
        MapRuleProvider<Area, Any>().apply {

            addRule(A.A1, HelperRules.A.generate(10))


        }.withHierarchy(simpleDiamond)
            .test(C.C1, "Hello" to 10)
    }

    @Test
    fun diamondDuplicateWithCoverage() {
        MapRuleProvider<Area, Any>().apply {

            addRule(A.A1, HelperRules.A.generate(10))
            addRule(B.B1, HelperRules.A.generate(10))
            addRule(B.B2, HelperRules.A.generate(10))


        }.withHierarchy(simpleDiamond)
            .test(C.C1, "Hello" to 10)
    }

    @Test
    fun diamondDuplicateWithoutCoverage() {
        MapRuleProvider<Area, Any>().apply {
            addRule(A.A1, HelperRules.A.generate(10))
            addRule(B.B1, HelperRules.A.generate(10))
            addRule(B.B2, HelperRules.A.generate(10))
        }.withHierarchy(diamondWithEmptyAreas)
            .test(C.C1, "Hello" to 10)
    }


    private fun HierarchicRuleProvider<Area, *>.test(area: Area, vararg expectedRules: Pair<String, Number>) {
        val composedRules = getComposedRules(area)
        expectedRules.forEach { (name, target) ->
            assertTrue(name in composedRules)
            assertEquals(target.toDouble(), composedRules.getTarget(name))
        }
    }
}