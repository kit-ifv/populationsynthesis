package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.rules.measurement.NamedMeasurement
import edu.kit.ifv.populationsynthesis.rules.provider.MapRuleProvider
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class LogicIndexerTest {
    @Test
    fun noDuplicateMeasures() {

        val ruleProvider = MapRuleProvider<Int, Any>().apply {
            addRule(1, spawnRule("A"))
            addRule(1, spawnRule("B"))
            addRule(2, spawnRule("B"))
        }
        val indexer = LogicIndexer.fromProvider(ruleProvider)
        assertEquals(indexer.allMeasurements().size, 2)
    }

    private fun spawnRule(id: String): Rule<Any> {
        return Rule(42.0, logic = NamedMeasurement.numeric(id) {
            1.0
        } )
    }
}