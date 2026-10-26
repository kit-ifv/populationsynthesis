package edu.kit.ifv.populationsynthesis.rules

/**
 * A mutable rule set that aggregates rules with identical logic identifiers.
 *
 * When a rule is added whose logic identifier already exists in the set,
 * the rule is merged with the existing one by summing their target values.
 * The underlying logic is preserved and only the target is accumulated.
 *
 * This strategy is useful when multiple independent contributions to the
 * same logical rule should be combined rather than rejected or replaced.
 */
class AggregatingRuleSet<T> : MutableRuleSet<T>() {
    override fun add(rule: Rule<T>) {
        val existingTarget = rules[rule.logic.identifier]?.target ?: 0.0
        rules[rule.logic.identifier] = rule + existingTarget
    }
}