package datasource

import edu.kit.ifv.populationsynthesis.domain.rules.CensusDemographyRules
import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class CensusDemographyTest {
    @Test

    fun compile() {
        val demography = CensusDemographyRules.fromResource()
        val targets = demography[ARSKey.MARNE_NORDSEE] ?: fail("Demography not found")
        assertEquals(targets.Insgesamt_!!, 12848)

    }
}