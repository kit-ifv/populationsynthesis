package edu.kit.ifv.populationsynthesis.algorithms

data class IPUOutput<I>(
    val element: I,
    val amount: Double,
) {
    fun discretize(target: Int) = IntegerIPUOutput(element, target)
}