package edu.kit.ifv.populationsynthesis.optimizers.ojalgo

import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import org.ojalgo.optimisation.ExpressionsBasedModel
import kotlin.collections.get

class WeightConsideringOJSolverBuilder(
    model: ExpressionsBasedModel = ExpressionsBasedModel(),
    val lambda: Double = 1.0
): OJSolverBuilder(model) {
    private val similarityObjective =
        model.addExpression("distribution_similarity").weight(lambda)
    private val total =
        model.addVariable("total").lower(0.0)
    lateinit var originalDistribution: Map<ScalableVector, Double>

    fun load(originalDistribution: Map<ScalableVector, Double>) {
        this.originalDistribution = originalDistribution
    }

    override fun afterConfigHook() {

        // total = sum_i x_i
        val totalConstraint =
            model.addExpression("total_definition").level(0.0)

        totalConstraint.set(total, 1.0)

        vectorVariables.values.forEach { variable ->
            totalConstraint.set(variable, -1.0)
        }

        var totalSquareCoefficient = 0.0

        vectorVariables.forEach { (vector, variable) ->

            val p = originalDistribution[vector] ?: 0.0.also {
                println("Unknown square coefficient for $vector")
            }

            /*
             * lambda * (x_i - p_i * N)^2
             *
             * = lambda * (
             *      x_i²
             *    - 2 p_i x_i N
             *    + p_i² N²
             * )
             */

            similarityObjective.set(
                variable,
                variable,
                1.0
            )

            similarityObjective.set(
                variable,
                total,
                -2.0 * p
            )

            totalSquareCoefficient += p * p
        }

        similarityObjective.set(
            total,
            total,
            totalSquareCoefficient
        )
    }
}