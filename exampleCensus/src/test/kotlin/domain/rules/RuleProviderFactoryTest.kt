package domain.rules

import edu.kit.ifv.populationsynthesis.domain.area.HierarchyFactory
import edu.kit.ifv.populationsynthesis.domain.rules.RuleProviderFactory
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class RuleProviderFactoryTest {
    @Test
    fun properKeys() {
        val keys = RuleProviderFactory.keys

        val graph = HierarchyFactory.fromARSKeyset(keys)
        assertEquals(12439, keys.size)
    }

}