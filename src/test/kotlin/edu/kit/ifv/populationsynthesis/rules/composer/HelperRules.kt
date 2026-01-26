package edu.kit.ifv.populationsynthesis.rules.composer

import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.contribution.NamedContribution

internal class HelperRules(val text: String) {

    fun generate(target: Number): Rule<Any> {
        return Rule(target.toDouble(), NamedContribution.Companion.boolean(text) { true })
    }

    companion object {
        val A = HelperRules("Hello")
        val B = HelperRules("World")
        val C = HelperRules("Else")
    }
}