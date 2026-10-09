package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.MatrixSolver
import edu.kit.ifv.populationsynthesis.algorithms.rounding.RoundingStrategy
import edu.kit.ifv.populationsynthesis.algorithms.rounding.withCallback
import edu.kit.ifv.populationsynthesis.algorithms.sampling.ListSampler
import edu.kit.ifv.populationsynthesis.algorithms.sampling.PickWithReplacement
import kotlin.random.Random


data class HierarchicDistributionConfig(
    val matrixSolver: MatrixSolver = MatrixSolver.legacy,
    val random: Random = Random(42),
    val samplerAlgorithm: ListSampler = PickWithReplacement(random),
    val roundingStrategy: RoundingStrategy = RoundingStrategy.cumulative.withCallback { ints, task ->
        val lookup = task.vectors.zip(ints).toMap()
        task.observers.forEach { observer ->
            val integerResult = observer.vectors.sumOf { (lookup[it] ?: 0) * it.baseVector[observer.observedIndex] }
            println("expected = ${observer.expected}, doubleResult = ${observer.actual}, intResult = $integerResult")

        }
    }
)
