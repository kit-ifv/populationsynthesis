package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.rules.contribution.LogicIdentifier

class MutableRuleSet<T> private constructor(
    private val rules: MutableMap<LogicIdentifier, Rule<T>>
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