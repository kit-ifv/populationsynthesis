package edu.kit.ifv.populationsynthesis.rules

import org.jetbrains.annotations.TestOnly

/**
 * Defines a rule for population synthesis, which describes a condition to be met by the synthetic population.
 * These conditions are often based on observations from external surveys. Example rules include:
 * - "The number of men aged 20–30 in a specific area should be 9001."
 * - "Only 1000 agents should have an income of exactly 42€."
 *
 * The implementation logic of a rule is left as an interface for external implementations. All rules must have a
 * numeric target that specifies the desired outcome.
 *
 * @property target The numeric value representing the desired state or outcome defined by the rule.
 * @property descriptiveText A human-readable name or description of the rule.
 * @param T The shared property type required by a household to be evaluated against this rule.
 */
interface RuleOld<in T> {
    val target: Double
    val description: String

    val logic: Nothing

    /**
     * Evaluates the extent to which a given [element] satisfies the condition defined by the rule.
     * For example:
     * - A rule like "Number of people aged 10–50" could return a value in the range [0, ∞].
     * - A rule like "Household size is exactly 5" could return either 0 or 1.
     *
     * @param element The household to evaluate.
     * @return An integer representing the contribution of the household to the rule's target.
     */
    fun evaluate(element: T): Int

    fun evaluate(elements: Collection<T>): Int = elements.sumOf { evaluate(it) }

    /**
     * Determines whether a given [element] contributes to the rule's target.
     *
     * @param element The household to check.
     * @return `true` if the household contributes to the rule's target; `false` otherwise.
     */
    fun appliesTo(element: T): Boolean = evaluate(element) != 0

    /**
     * Calculates the difference (offset) between the desired [target] and the aggregate contributions from a
     * collection of households.
     *
     * @param output A collection of households to evaluate.
     * @return The difference between the target and the sum of contributions from the households.
     */
    fun verify(output: Collection<T>): Double {
        return target.toDouble() - output.sumOf { evaluate(it) }
    }

    /**
     * Filters a collection of households to include only those that contribute to the rule's target.
     *
     * @param target The collection of households to filter.
     * @return A list of households that contribute to the rule.
     */
    @TestOnly
    fun filter(target: Collection<T>): List<@UnsafeVariance T> {
        return target.filter { appliesTo(it) }
    }

    fun descriptiveText() = "[$description] expected = $target"
}


