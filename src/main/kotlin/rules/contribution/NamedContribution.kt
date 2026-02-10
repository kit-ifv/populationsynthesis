package edu.kit.ifv.populationsynthesis.rules.contribution

import edu.kit.ifv.populationsynthesis.rules.Rule

/**
 * The only implementation of Rule Logic. This class is protected because the comparision logic is wired to the
 * identifier string, which should not be operated upon.
 *
 * Equality/hash over identifier.
 *
 * Why aren't we using the contribution origin as equality check? Because the same rules could theoretically be
 * independently spawned from different implementors.
 */

class NamedContribution<in T> private constructor(
    val identifier: LogicIdentifier,
    val logic: Contribution<T>,
) : Contribution<T> by logic {


    override fun equals(other: Any?): Boolean {
        if (other !is NamedContribution<*>) return false
        return identifier == other.identifier
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }

    fun withTarget(target: Double): Rule<T> {
        return Rule(target, this)
    }

    override fun toString(): String {
        return "NamedContribution($identifier)"
    }

    companion object {
        fun <T> boolean(
            identifier: String,
            logic: (T) -> Boolean
        ) =
            NamedContribution<T>(LogicIdentifier(identifier)) {
                if (logic(it)) 1.0 else 0.0
            }

        fun <T> numeric(identifier: String, logic: (T) -> Number) =
            NamedContribution<T>(LogicIdentifier(identifier)) {
                logic(it).toDouble()
            }

    }
}

