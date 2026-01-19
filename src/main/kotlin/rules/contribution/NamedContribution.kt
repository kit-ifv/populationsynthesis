package edu.kit.ifv.populationsynthesis.rules.contribution

import edu.kit.ifv.populationsynthesis.rules.Rule
import kotlin.text.get

/**
 * The only implementation of Rule Logic. This class is protected because the comparision logic is wired to the
 * identifier string, which should not be operated upon.
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


    companion object {
        internal inline fun <T> boolean(
            identifier: String,
            crossinline logic: (T) -> Boolean
        ) =
            NamedContribution<T>(LogicIdentifier.create(identifier)) {
                if (logic(it)) 1.0 else 0.0
            }

        internal inline fun <T> numeric(identifier: String, crossinline logic: (T) -> Number) =
            NamedContribution<T>(LogicIdentifier.create(identifier)) {
                logic(it).toDouble()
            }

    }
}

/**
 * This string wrapper represents a random name that has been given to a contribution function. The constructor also
 * registers the logic name in the registry, to resolve ambiguities and help with debugging.
 */
@JvmInline
value class LogicIdentifier private constructor(val text: String) {

    companion object {
        private val registeredRules: MutableMap<String, LogicIdentifier> = mutableMapOf()

        operator fun get(text: String): LogicIdentifier? = registeredRules[text]

        fun getValue(text: String): LogicIdentifier = registeredRules.getValue(text)
        internal operator fun set(text: String, identifier: LogicIdentifier) = { registeredRules[text] = identifier }

        fun create(text: String) = registeredRules.getOrPut(text) { LogicIdentifier(text) }
    }
}

