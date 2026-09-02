package edu.kit.ifv.populationsynthesis.optimizers.ortools

import com.google.ortools.linearsolver.MPSolver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver

class LenientORToolsSolver(
    solver: MPSolver = MPSolver.createSolver("GLOP"),
    upperBound: Double = 80_000_000.0,
): ORToolsSolver(solver, upperBound) {
    private val objective = solver.objective()
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
        objective.setCoefficient(delta, 1.0)
    }
}