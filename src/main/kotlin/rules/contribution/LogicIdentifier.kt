package edu.kit.ifv.populationsynthesis.rules.contribution

/**
 * This string wrapper represents a random name that has been given to a contribution function. The constructor also
 * registers the logic name in the registry, to resolve ambiguities and help with debugging.
 */
@JvmInline
value class LogicIdentifier(val text: String) {


}