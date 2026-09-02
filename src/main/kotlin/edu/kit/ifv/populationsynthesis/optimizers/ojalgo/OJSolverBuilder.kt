package edu.kit.ifv.populationsynthesis.optimizers.ojalgo

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import edu.kit.ifv.populationsynthesis.optimizers.LoadMatrixProblemToSolver
import org.ojalgo.optimisation.ExpressionsBasedModel
import org.ojalgo.optimisation.Variable


typealias OJVariable = Variable
class OJSolverBuilder(
    private val model: ExpressionsBasedModel = ExpressionsBasedModel()
): LoadMatrixProblemToSolver<OJSolverBuilder.Result> {
    inner class Result(
        val deltas: List<OJVariable>
    )
    private val objective = model.addExpression("squared_deviation").weight(1.0)
    private val vectorVariables: MutableMap<Vector, OJVariable> = mutableMapOf()
    private val deltaVariables: MutableList<OJVariable> = mutableListOf()
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

        objective.set(delta, delta, 1.0)
    }
    override fun solve(): Pair<Map<Vector, Double>, Result> {
        model.minimise()
        return vectorVariables.mapValues { it.value.value.toDouble() } to Result(deltaVariables)
    }
}