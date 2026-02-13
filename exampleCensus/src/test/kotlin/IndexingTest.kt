import edu.kit.ifv.populationsynthesis.domain.population.Population
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu.EquivalenceClassIPU
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu.RuleObserverBuilder
import edu.kit.ifv.populationsynthesis.domain.area.HierarchyFactory
import edu.kit.ifv.populationsynthesis.domain.rules.RuleProviderFactory
import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import kotlin.test.Test

class IndexingTest {
    @Test
    fun testVectorCreation() {
        val hierarchy = HierarchyFactory.marneExample()
        val rules = RuleProviderFactory.marneExample()
        val hierarchicRuleProvider = rules.withHierarchy(hierarchy)
        val population = Population.fromPersonInfo()
        val ipu = EquivalenceClassIPU(hierarchicRuleProvider, population.households)

        val ruleObserverBuilder = RuleObserverBuilder(hierarchicRuleProvider)
        val indexedRules = ruleObserverBuilder.getLogics(listOf(ARSKey.MARNE_NORDSEE, ARSKey.DIEKHUSEN_FAHRSTEDT))
        ipu.spawnVectorsFrom(indexedRules)

    }
}