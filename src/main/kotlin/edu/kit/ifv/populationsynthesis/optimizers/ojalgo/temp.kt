package edu.kit.ifv.populationsynthesis.optimizers.ojalgo

import org.ojalgo.optimisation.ExpressionsBasedModel
import org.ojalgo.optimisation.Optimisation

fun solve(
    vectors: List<List<Pair<Int, Double>>>,
    targets: List<Double> ,
): Solution {
    val model = ExpressionsBasedModel()

    val variables = vectors.indices.map { index ->
        model.addVariable("v_$index").lower(0.0)
    }

    val deltas = targets.indices.associate { index ->
        val name: String = "delta_$index"
        name to model.addVariable(name).lower(0.0)
    }

    val objective = model.addExpression("squared_deviation").weight(1.0)

    targets.forEachIndexed { ruleIndex, target ->
        val delta = deltas.getValue("delta_$ruleIndex")

        val lower = model.addExpression("lower_$ruleIndex").lower(target)
        val upper = model.addExpression("upper_$ruleIndex").upper(target)

        vectors.forEachIndexed { variableIndex, vector ->
            val coefficient =
                vector.firstOrNull { it.first == ruleIndex }?.second ?: 0.0

            if (coefficient != 0.0) {
                lower.set(variables[variableIndex], coefficient)
                upper.set(variables[variableIndex], coefficient)
            }
        }

        lower.set(delta, 1.0)  // Ax + δ ≥ target
        upper.set(delta, -1.0) // Ax - δ ≤ target

        objective.set(delta, delta, 1.0) // δ²
    }

    val result =  model.minimise()
    val solution: Solution = Solution(
        state = result.state,
        objective = result.value,
        values = variables.map { result.doubleValue(it.index.index) },
        deltas = deltas.values.map { result.doubleValue(it.index.index) },
    )
    val residuals = targets.mapIndexed { ruleIndex, target ->
        val actual = vectors.indices.sumOf { variableIndex ->
            val coefficient = vectors[variableIndex]
                .firstOrNull { it.first == ruleIndex }
                ?.second ?: 0.0

            coefficient * solution.values[variableIndex]
        }

        actual - target
    }

    residuals.forEachIndexed { index, residual ->
        println("Rule $index: residual=$residual")
    }
    return solution
}
data class Solution(
    val state: Optimisation.State,
    val objective: Double,
    val values: List<Double>,
    val deltas: List<Double>,
)
