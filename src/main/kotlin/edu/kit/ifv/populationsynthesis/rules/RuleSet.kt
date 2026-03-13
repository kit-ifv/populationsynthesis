package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.measurement.MeasurementDefinition

/**
 * A rule set guarantees no duplicate logic: There can be only one named contribution per rule.
 * While theoretically possible to have two distinct named contribution with different identifier (while having
 * the same logic)
 *
 * A duplication in logic usually means a programmatic error, as it makes no sense that two rules are defined
 * for the same logic (Think a region with
 * Rule 1: people aged [0,15] = 42.0 and another
 * Rule 2: people aged [0,15] = 13.0)
 * That does not make sense. If you ever need such a constellation I apologize because in all my use cases i needed
 * the uniqueness and allowing for duplicates only introduced an error source.
 */

interface RuleSet<in T> : Set<Rule<T>> {
    operator fun get(key: String) = get(LogicIdentifier(key))
    operator fun get(key: LogicIdentifier): Rule<T>?

    fun getValue(key: String): Rule<T> = get(key) ?: throw NoSuchElementException(key)

    operator fun contains(key: MeasurementDefinition<*>) = contains(key.generateDescription())
    operator fun contains(key: String) = contains(LogicIdentifier(key))
    operator fun contains(key: LogicIdentifier): Boolean

    fun getTarget(key: MeasurementDefinition<*>) = getTarget(key.generateDescription())
    fun getTarget(key: String) = getTarget(LogicIdentifier(key))
    fun getTarget(key: LogicIdentifier): Double? = get(key)?.target

}


