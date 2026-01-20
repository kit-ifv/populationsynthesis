package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.rules.contribution.LogicIdentifier

/**
 * A rule set guarantees no duplicate logic: There can be only one named contribution per rule.
 * While theoretically possible to have two distinct named contribution with different identifier (while having
 * the same logic)
 *
 * For most programmatic tasks it is sufficient to know that a rule set exists.
 */

interface RuleSet<T>: Set<Rule<T>> {
    operator fun get(key: String) = get(LogicIdentifier(key))
    operator fun get(key: LogicIdentifier): Rule<T>?
    operator fun contains(key: String) = contains(LogicIdentifier(key))
    operator fun contains(key: LogicIdentifier): Boolean

    companion object {
        fun <T> create(rules: Map<LogicIdentifier, Rule<T>>): RuleSet<T> {
            return MutableRuleSet.create(rules.toMutableMap())
        }
    }
}

class MutableRuleSet<T> private constructor(
    private val rules: MutableMap<LogicIdentifier, Rule<T>>
) : AbstractSet<Rule<T>>(), RuleSet<T> {

    private constructor(rules: Collection<Rule<T>>) : this(rules.associateBy { it.logic.identifier }.toMutableMap())

    override val size: Int
        get() = rules.size

    override fun iterator(): Iterator<Rule<T>> {
        return rules.values.iterator()
    }

    override fun toString(): String {
        return rules.values.toString()
    }


    fun add(rule: Rule<T>) {
        val existingTarget = rules[rule.logic.identifier]?.target ?: 0.0
        rules[rule.logic.identifier] = rule + existingTarget
    }

    fun add(rules: Collection<Rule<T>>) {
        for (rule in rules) {
            add(rule)
        }
    }
    operator fun plus(rules: Collection<Rule<T>>): MutableRuleSet<T> {
        return MutableRuleSet(this.rules.toMutableMap()).apply {
            add(rules)
        }
    }


    override fun get(key: LogicIdentifier): Rule<T>? {
        return rules[key]
    }

    override fun contains(key: LogicIdentifier): Boolean {
        return key in rules
    }


    companion object {
        fun <T> create(
            rules: Collection<Rule<T>>,
            accumulator: (Collection<Rule<T>>) -> Rule<T> = Collection<Rule<T>>::sumRule
        ): MutableRuleSet<T> {
            val groupedRules = rules.groupBy { it.logic }.values
            return MutableRuleSet(groupedRules.map { accumulator(it) })
        }

        fun <T> create(rules: Map<LogicIdentifier, Rule<T>>): MutableRuleSet<T> {
            return MutableRuleSet(rules.toMutableMap())
        }
    }
}