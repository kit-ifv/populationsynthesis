package edu.kit.ifv.populationsynthesis.rules

/**
 * A mutable rule set that allows rules to be redefined.
 *
 * If a rule with the same logic identifier already exists, the existing rule
 * is silently replaced by the new one.
 *
 * This strategy is useful when later definitions should take precedence
 * over earlier ones.
 */
class OverwritableRuleSet<T> : MutableRuleSet<T>() {
    override fun add(rule: Rule<T>) {
        rules[rule.logic.identifier] = rule
    }
}