package edu.kit.ifv.populationsynthesis.rules

class Rule<in T>(val target: Double, val logic : NamedContribution<T>) {
    fun evaluate(element: T): Double = logic.amount(element)
    fun evaluate(elements: Collection<T>): Double = elements.sumOf { evaluate(it) }

    fun appliesTo(element: T): Boolean = evaluate(element) != 0.0

    fun verify(output: Collection<T>): Double = target - evaluate(output)

    override fun toString(): String {
        return "Rule(target=$target, logic=${logic.identifier})"
    }

}

