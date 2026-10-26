package edu.kit.ifv.populationsynthesis.rules.composer

import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.measurement.NamedMeasurement

internal class RulesHelper(val text: String) {

    fun generate(target: Number): Rule<Any> {
        return Rule(target.toDouble(), NamedMeasurement.Companion.boolean(text) { true })
    }

    companion object {
        val A = RulesHelper("Hello")
        val B = RulesHelper("World")
        val C = RulesHelper("Else")
    }
}