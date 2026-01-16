package edu.kit.ifv.populationsynthesis.rules

/**
 * A rule generator is able to generate a list of rules. What exactly is up to the implementation
 */

interface RuleGenerator<T> {

    fun generateRules() : List<Rule<T>>
}

