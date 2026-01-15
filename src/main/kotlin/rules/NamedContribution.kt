package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.rules.contribution.Contribution

/**
 * The only implementation of Rule Logic. This class is protected because the comparision logic is wired to the
 * identifier string, which should not be operated upon.
 *
 * Why aren't we using the contribution origin as equality check? Because the same rules could theoretically be
 * independently spawned from different implementors. Also Contribution Origin is an interface which makes equals
 * and hashcode unenforceable.
 */
// Hide the constructor from idiots.
class NamedContribution<in T> internal constructor(
    val identifier: String,
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


    companion object {
        internal inline fun <T> boolean(
            identifier: String,
            crossinline logic: (T) -> Boolean) =
            NamedContribution<T>(identifier) {
                if (logic(it)) 1.0 else 0.0
            }

        internal inline fun <T> numeric(identifier: String, crossinline logic: (T) -> Number) =
            NamedContribution<T>(identifier) {
                logic(it).toDouble()
            }

    }
}