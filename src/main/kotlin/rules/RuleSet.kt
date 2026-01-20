package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.rules.contribution.LogicIdentifier

/**
 * A rule set guarantees no duplicate logic: There can be only one named contribution per rule.
 * While theoretically possible to have two distinct named contribution with different identifier (while having
 * the same logic)
 *
 * For most programmatic tasks it is sufficient to know that a rule set exists.
 */
class RuleSet<T> private constructor(
    private val rules: MutableMap<LogicIdentifier, Rule<T>>
) : Iterable<Rule<T>>, Map<LogicIdentifier, Rule<T>> by rules {

    private constructor(rules: Collection<Rule<T>>) : this(rules.associateBy { it.logic.identifier }.toMutableMap())
    override fun iterator(): Iterator<Rule<T>> = rules.values.iterator()
    operator fun get(key: String) = rules[LogicIdentifier[key]]

    fun getValue(key: String) = rules.getValue(LogicIdentifier.getValue(key))
    fun add(rule: Rule<T>) {
        val existingTarget = rules[rule.logic.identifier]?.target ?: 0.0
        rules[rule.logic.identifier] = rule + existingTarget
    }

    fun add(rules: Collection<Rule<T>>) {
        for (rule in rules) {
            add(rule)
        }
    }

    override fun toString(): String {
        return rules.values.toString()
    }

    operator fun plus(rules: Collection<Rule<T>>): RuleSet<T> {
        return RuleSet(this.rules.toMutableMap()).apply {
            add(rules)
        }
    }
    companion object {
        fun <T> create(
            rules: Collection<Rule<T>>,
            accumulator: (Collection<Rule<T>>) -> Rule<T> = Collection<Rule<T>>::sumRule
        ): RuleSet<T> {
            val groupedRules = rules.groupBy { it.logic }.values
            return RuleSet(groupedRules.map { accumulator(it) })
        }

        fun <T> create(rules: Map<LogicIdentifier, Rule<T>>): RuleSet<T> {
            return RuleSet(rules.toMutableMap())
        }
    }
}