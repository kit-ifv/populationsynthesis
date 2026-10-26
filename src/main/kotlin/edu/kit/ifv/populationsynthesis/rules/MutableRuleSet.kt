package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier

/**
 * A mutable rule set that enforces uniqueness of rule logic identifiers.
 *
 * Each rule is indexed by its [LogicIdentifier]. Adding a rule whose logic identifier
 * already exists in the set is considered an error and will result in an exception.
 *
 * This strict behavior avoids ambiguity by ensuring that at most one rule exists
 * per logic definition.
 */
open class MutableRuleSet<T> private constructor(
    protected val rules: MutableMap<LogicIdentifier, Rule<T>>
) : AbstractSet<Rule<T>>(), RuleSet<T> {

    private constructor(rules: Collection<Rule<T>>) : this(rules.associateBy { it.logic.identifier }.toMutableMap())

    constructor() : this(mutableMapOf())

    override val size: Int
        get() = rules.size

    override fun iterator(): Iterator<Rule<T>> {
        return rules.values.iterator()
    }

    override fun toString(): String {
        return rules.values.toString()
    }


    open fun add(rule: Rule<T>) {
        require(rule.logic.identifier !in rules) {
            "The rule logic ${rule.logic.identifier} has been already set. To avoid ambiguity I will simply crash"
        }
        rules[rule.logic.identifier] = rule
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

    fun clear() = rules.clear()

    companion object {

        fun <T> empty(): MutableRuleSet<T> = MutableRuleSet(mutableMapOf())
        fun <T> create(
            rules: Collection<Rule<T>>,
            accumulator: (Collection<Rule<T>>) -> Rule<T> = Collection<Rule<T>>::sumRule
        ): MutableRuleSet<T> {
            val groupedRules = rules.groupBy { it.logic }.values
            val accumulatedRules = groupedRules.map {
                accumulator(it)
            }

            return MutableRuleSet(accumulatedRules)
        }

        fun <T> create(rules: Map<LogicIdentifier, Rule<T>>): MutableRuleSet<T> {
            return MutableRuleSet(rules.toMutableMap())
        }
    }
}


