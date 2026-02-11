package datasource

import edu.kit.ifv.populationsynthesis.datasource.CensusHouseholdRuleCollector
import edu.kit.ifv.populationsynthesis.datasource.createRuleProvider
import edu.kit.ifv.populationsynthesis.datasource.input.ARSKey
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class CensusHouseholdDataTest {

    private val data = CensusHouseholdRuleCollector.fromResource()

    @Test
    fun properLoadedTargets() {
        val targetData = data[ARSKey.MARNE_NORDSEE] ?: fail("Marne is not in the data")
        assertEquals(targetData.HH_SIZE_NAT__1, 2590)
        assertEquals(targetData.HH_SIZE_NAT__2, 2329)
        assertEquals(targetData.HH_SIZE_NAT__3, 753)
        assertEquals(targetData.HH_SIZE_NAT__4, 547)
        assertEquals(targetData.HH_SIZE_NAT__5, 214)
        assertEquals(targetData.HH_SIZE_NAT__6, 80)
    }


    @Test
    fun noDuplicateRuleConstruction() {


        assertThrows<IllegalArgumentException> {
            createRuleProvider {
                loadFromOtherRuleProvider(data.sizeProvider) {
                    it.key == ARSKey.MARNE_NORDSEE
                }
                loadFromOtherRuleProvider(data.size6plusProvider) {
                    it.key == ARSKey.MARNE_NORDSEE
                }
            }
        }


    }
}