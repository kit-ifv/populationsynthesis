package edu.kit.ifv.populationsynthesis.rules

/**
 * A rule generator is able to generate a RuleSet. What exactly is up to the implementation
 */

fun interface RuleGenerator<T> {
    fun generateRules() : RuleSet<T>
}

