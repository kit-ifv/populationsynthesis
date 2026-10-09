package domain.rules

import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import edu.kit.ifv.populationsynthesis.domain.area.HierarchyFactory
import edu.kit.ifv.populationsynthesis.domain.rules.CensusRuleProviderFactory
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.math.pow
import kotlin.test.Test

class CensusRuleProviderFactoryTest {
    @Test
    fun properKeys() {
        val keys = CensusRuleProviderFactory.keys

        val graph = HierarchyFactory.fromARSKeyset(keys)
        assertEquals(12439, keys.size)
    }
    @Test
    fun flexibleLoading() {

        val numbers = (1..6).map {
            2.0.pow(it).toInt() - 1
        }
        numbers.forEach {
            val a = CensusRuleProviderFactory.loadFlatProviderWithNumber(ARSKey.GERMANY, it)
            println(a.getRules(ARSKey.GERMANY))
        }
        val a = CensusRuleProviderFactory.loadFlatProviderWithNumber(ARSKey.MARNE_NORDSEE, 1)
    }
}