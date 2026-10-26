package edu.kit.ifv.populationsynthesis.algorithms

data class WeightedElement<I>(
    val element: I,
    val amount: Double,
) {
    fun discretize(target: Int) = IntegerWeightedElement(element, target)
}