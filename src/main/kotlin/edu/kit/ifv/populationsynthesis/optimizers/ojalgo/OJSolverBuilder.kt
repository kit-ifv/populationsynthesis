package edu.kit.ifv.populationsynthesis.optimizers.ojalgo

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
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
    class Result(
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
            val coefficient = it.baseVector[rowIndex]
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

