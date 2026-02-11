import edu.kit.ifv.populationsynthesis.datasource.HierarchyFactory
import edu.kit.ifv.populationsynthesis.datasource.RuleProviderFactory
import edu.kit.ifv.populationsynthesis.datasource.input.ARSKey
import edu.kit.ifv.populationsynthesis.measurements.PersonAgeDefinition
import edu.kit.ifv.populationsynthesis.measurements.asHouseholdDefinition
import kotlin.test.Test
import kotlin.test.assertEquals

class HierarchicRuleProviderTest {
    @Test
    fun runTest() {
        val hierarchy = HierarchyFactory.marneExample()

        val hierarchicRuleProvider = RuleProviderFactory.marneExample().withHierarchy(hierarchy)

        val rules = hierarchicRuleProvider.getComposedRules(ARSKey.MARNE_NORDSEE)
        val output = rules[PersonAgeDefinition(0..2).asHouseholdDefinition().createNamedMeasurement().identifier]
        val expected = listOf(
            14,
            32,
            17,
            5,
            22,
            147,
            14,
            14,
            5,
            4,
            8,
        ).sum()
        assertEquals(expected.toDouble(), output!!.target)
    }
}