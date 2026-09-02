package edu.kit.ifv.populationsynthesis.optimizers.ortools

import com.google.ortools.linearsolver.MPSolver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver

class DirectORToolsSolver(
    solver: MPSolver = MPSolver.createSolver("GLOP"),
    upperBound: Double = 80_000_000.0,
): ORToolsSolver(solver, upperBound) {
    override fun addObserver(observer: RuleObserver) {
        val target = observer.expected
        val rowIndex = observer.observedIndex
        val constraint = solver.makeConstraint(target, target, "c_${rowIndex}")
        observer.vectors.forEach{
            constraint.setCoefficient(vectorVariables.getValue(it), it.attributeForIndex(rowIndex))
        }
    }
}