package edu.kit.ifv.populationsynthesis.optimizers.ortools

import com.google.ortools.linearsolver.MPObjective
import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import java.lang.Math.abs
import kotlin.collections.get

open class LenientORToolsSolver(
    solver: MPSolver = MPSolver.createSolver("GLOP"),
    upperBound: Double = 80_000_000.0,
): ORToolsSolver(solver, upperBound) {
    protected val objective: MPObjective = solver.objective()
    override fun addObserver(observer: RuleObserver) {
        val target = observer.expected
        val rowIndex = observer.observedIndex
        val delta = solver.makeNumVar(.0, MPSolver.infinity(), "delta_$rowIndex")
        val lower = solver.makeConstraint(target, MPSolver.infinity(), "lowerdelta_$rowIndex")
        val upper = solver.makeConstraint(-MPSolver.infinity(), target, "upperdelta_$rowIndex")
        observer.vectors.forEach{
            val variable = vectorVariables.getValue(it)
            val coefficient = it.attributeForIndex(rowIndex)
            lower.setCoefficient(variable, coefficient)
            upper.setCoefficient(variable, coefficient)
        }
        lower.setCoefficient(delta, 1.0)
        upper.setCoefficient(delta, -1.0)
        objective.setCoefficient(delta, 1.0 )
    }
}


class WeightRegardingLenientORToolsSolver(
    solver: MPSolver = MPSolver.createSolver("GLOP"),
    upperBound: Double = 80_000_000.0,
    private val lambda: Double = 1.0,

): LenientORToolsSolver(solver, upperBound) {

    constructor(solverName: String, upperBound: Double = 80_000_000.0, lambda: Double = 1.0): this(
        MPSolver.createSolver(solverName), upperBound, lambda
    )
    private val total: MPVariable =  solver.makeNumVar(.0, MPSolver.infinity(), "total")


    override fun addObserver(observer: RuleObserver) {
        val target = observer.expected
        val rowIndex = observer.observedIndex
        val delta = solver.makeNumVar(.0, MPSolver.infinity(), "delta_$rowIndex")
        val lower = solver.makeConstraint(target, MPSolver.infinity(), "lowerdelta_$rowIndex")
        val upper = solver.makeConstraint(-MPSolver.infinity(), target, "upperdelta_$rowIndex")
        observer.vectors.forEach{
            val variable = vectorVariables.getValue(it)
            val coefficient = it.attributeForIndex(rowIndex)
            lower.setCoefficient(variable, coefficient)
            upper.setCoefficient(variable, coefficient)
        }
        lower.setCoefficient(delta, 1.0)
        upper.setCoefficient(delta, -1.0)
        objective.setCoefficient(delta, 1.0 / target.coerceAtLeast(1.0))
//        objective.setCoefficient(delta, 1.0 / target)
    }
    lateinit var originalDistribution: Map<ScalableVector, Double>
    fun load(  originalDistribution: Map<ScalableVector, Double>) {
        this.originalDistribution = originalDistribution
    }

    override fun afterConfigHook() {

        val totalConstraint  = solver.makeConstraint(.0, .0, "total_definition")
        totalConstraint.setCoefficient(total, 1.0)
        vectorVariables.values.forEach {
            totalConstraint.setCoefficient(it, -1.0)
        }
        vectorVariables.forEach { (vector, variable) ->
            addDeviationConstraints(
                variable,
                originalDistribution[vector]?: .0,
                lambda
            )
        }
    }
    private fun addDeviationConstraints(
        variable: MPVariable,
        originalShare: Double,
        weight: Double = 1.0,
    ) {
        val delta = solver.makeNumVar(.0, MPSolver.infinity(), "delta_${variable.name()}")
        val upper = solver.makeConstraint(
            -MPSolver.infinity(),
            0.0,
            "upper_${delta.name()}",
        )
        upper.setCoefficient(variable, 1.0)
        upper.setCoefficient(total, -originalShare)
        upper.setCoefficient(delta, -1.0)

        // p_i * total - x_i <= deviation
        val lower = solver.makeConstraint(
            -MPSolver.infinity(),
            0.0,
            "lower_${delta.name()}",
        )
        lower.setCoefficient(variable, -1.0)
        lower.setCoefficient(total, originalShare)
        lower.setCoefficient(delta, -1.0)


        objective.setCoefficient(delta, weight)
    }

}