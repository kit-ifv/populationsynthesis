package edu.kit.ifv.populationsynthesis.algorithms.ipu

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import kotlin.math.pow


object Kaczmarz: GenericIPU {
    override fun run(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>,
    ) {
        val normalizationFactor = observers.associateWith {
            it.vectors.sumOf { v -> v.attributeForIndex(it.observedIndex).pow(2) } // Theoretically this could be private in observer
        }


        var counter = 0
        while (counter < 1000) {
            observers.forEach { observer ->
                val alpha = (observer.expected - observer.sum()) / normalizationFactor[observer]!!
                vectors.forEach { vector ->
                    vector.scalar += alpha * vector.attributeForIndex(observer.observedIndex)
                }
            }
            counter++
        }
    }
}