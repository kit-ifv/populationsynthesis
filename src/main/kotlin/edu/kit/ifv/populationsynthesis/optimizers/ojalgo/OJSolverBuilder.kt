package edu.kit.ifv.populationsynthesis.optimizers.ojalgo

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import edu.kit.ifv.populationsynthesis.optimizers.LoadMatrixProblemToSolver
import org.ojalgo.optimisation.Expression
import org.ojalgo.optimisation.ExpressionsBasedModel
import org.ojalgo.optimisation.Variable
import org.ojalgo.optimisation.convex.ConvexSolver


typealias OJVariable = Variable
open class OJSolverBuilder(
    protected val model: ExpressionsBasedModel = ExpressionsBasedModel()
): LoadMatrixProblemToSolver<OJSolverBuilder.Result> {
    inner class Result(
        val deltas: List<OJVariable>
    )
    protected val objective: Expression = model.addExpression("squared_deviation").weight(1.0)
    protected val vectorVariables: MutableMap<Vector, OJVariable> = mutableMapOf()
    protected val deltaVariables: MutableList<OJVariable> = mutableListOf()
    override fun addVectorVariable(vector: Vector) {
        val v = model.addVariable("v_${vectorVariables.keys.size}").lower(.0)
        vectorVariables[vector] = v
    }

    override fun addObserver(observer: RuleObserver) {
        val target = observer.expected
        val rowIndex = observer.observedIndex
        val delta = model.addVariable("delta_$rowIndex").lower(.0)
        deltaVariables.add(delta)
        require(observer.vectors.all { it in vectorVariables.keys }) { "All vectors need to be registered" }
        val lower = model.addExpression("lower_${rowIndex}").lower(target)
        val upper = model.addExpression("upper_${rowIndex}").upper(target)

        observer.vectors.forEach{
            val coefficient = it.attributeForIndex(rowIndex)
            require(coefficient != .0) {"A coefficient of .0 should never be registered in an observer"}
            val variable = vectorVariables.getValue(it)
            lower.set(variable, coefficient)
            upper.set(variable, coefficient)
        }
        lower.set(delta, 1.0)
        upper.set(delta, -1.0)


        objective.set(delta, delta, 1.0 )
    }
    override fun solve(): Pair<Map<Vector, Double>, Result> {
        require(model.validate()) { "Invalid ojAlgo model" }
        model.options.convex()
            .algorithm(ConvexSolver.Algorithm.ADMM)
        val result = model.minimise()
        require(result.state.isFeasible) {
            "ojAlgo failed: ${result.state}, result=$result"
        }
        return vectorVariables.mapValues { it.value.value.toDouble() } to Result(deltaVariables)
    }
}

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