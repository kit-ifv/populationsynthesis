package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.datastructures.linalg.strictlyPositiveSum
import edu.kit.ifv.populationsynthesis.utils.NumericProbabilityDistribution
import kotlin.math.acos

data class SolverStateInfo(
    val theta: Double,
    val euclidianDistance: Double,
    val dimensionErrors: List<DimensionError>,
    var kullbackLeiblerDivergence: Double? = null,
    var jensenShannonDivergence: Double? = null,
    var totalVariationDistance: Double? = null,
    var l1Distance: Double? = null,
    var l2Distance: Double? = null,
    var hellingerDistance: Double? = null,
    var cosineSimilarity: Double? = null,
    var nonzeroVectors: Int? = null,
) {

    val rootMeanSquaredError: Double
        get() = kotlin.math.sqrt(
            dimensionErrors
                .map { it.residual * it.residual }
                .average()
        )

    val rootMeanSquaredPercentError: Double
        get() {
            val relativeSquaredErrors = dimensionErrors
                .mapNotNull { it.relativeError }
                .map { it * it }

            return if (relativeSquaredErrors.isEmpty()) {
                0.0
            } else {
                kotlin.math.sqrt(relativeSquaredErrors.average()) * 100.0
            }
        }

    companion object {
        fun fromSolverState(solverState: SolverState, original: NumericProbabilityDistribution?): SolverStateInfo {
            val actual = solverState.vectors.strictlyPositiveSum()
            val target = solverState.target
            val residual = target - actual

            val errors = (0 until target.size).map { i ->
                DimensionError(
                    target = target[i],
                    actual = actual[i],
                )
            }


            val solverStateInfo = SolverStateInfo(
                theta = acos(target.cosineSimilarity(actual)),
                euclidianDistance = residual.length(),
                dimensionErrors = errors,
            )

            original?.let { original ->
                val newDistribution = NumericProbabilityDistribution.fromWeights(solverState.vectors.map {
                    kotlin.math.max(
                        .0,
                        it.scalar
                    )
                })
                solverStateInfo.kullbackLeiblerDivergence = original.kullbackLeiblerDivergence(newDistribution)
                solverStateInfo.jensenShannonDivergence = original.jensenShannonDivergence(newDistribution)
                solverStateInfo.totalVariationDistance = original.totalVariationDistance(newDistribution)
                solverStateInfo.l1Distance = original.l1Distance(newDistribution)
                solverStateInfo.l2Distance = original.l2Distance(newDistribution)
                solverStateInfo.hellingerDistance = original.hellingerDistance(newDistribution)
                solverStateInfo.cosineSimilarity = original.cosineSimilarity(newDistribution)
                solverStateInfo.nonzeroVectors = solverState.vectors.count { it.scalar != .0 }
            }
            return solverStateInfo
        }
    }
}
