package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.rules.contribution.NamedContribution

class Rule<in T>(val target: Double, val logic : NamedContribution<T>) {
    fun contributionOf(element: T): Double = logic.amount(element)
    fun total(elements: Collection<T>): Double = elements.sumOf { contributionOf(it) }

    fun appliesTo(element: T): Boolean = contributionOf(element) != 0.0

    fun delta(output: Collection<T>): Double = target - total(output)

    override fun toString(): String {
        return "Rule(target=$target, logic=${logic.identifier})"
    }

}

