package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier


/**
 * Converts this collection of rules into a [RuleSet] using a strict uniqueness policy.
 *
 * For each logical key, exactly one rule must be present.
 * If multiple rules share the same logic, this function will fail fast to
 * protect against accidental ambiguity in rule definitions.
 *
 * If duplicate rules are intentional and you have a well-defined resolution
 * strategy, consider using [toRuleSet] with an accumulator function instead.
 */
fun <T> Collection<Rule<T>>.toRuleSet(): RuleSet<T> {
    return toRuleSet { matchingRules ->
        require(matchingRules.size == 1) {
            """
            Multiple rules were found where only one is allowed.

            One logic, one rule — clear and bright,
            Two rules appear? Then which is right?
            To guard your intent, we stop right here,
            Ambiguity is what we fear.

            If duplicates are what you intend,
            And order or merging is part of the plan,
            Then choose the forgiving path instead:
            the accumulator function will lend a hand.
            """.trimIndent()
        }
        matchingRules.first()
    }
}

/**
 * Converts this collection of rules into a [RuleSet] using a user-provided resolution strategy.
 *
 * This variant assumes that multiple rules may share the same logic and delegates the
 * responsibility of resolving such groups to the supplied [accumulator].
 *
 * The [accumulator] is invoked once per logical group and must deterministically
 * return a single rule representing the combined result.
 */
fun <T> Collection<Rule<T>>.toRuleSet(accumulator: (Collection<Rule<T>>) -> Rule<T> = Collection<Rule<T>>::sumRule): RuleSet<T> {
    return MutableRuleSet.create(this, accumulator)
}

fun <T> Map<LogicIdentifier, Rule<T>>.toRuleSet(): RuleSet<T> {
    return MutableRuleSet.create(this.toMutableMap())
}