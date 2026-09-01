package edu.kit.ifv.populationsynthesis.optimizers.ortools

import com.google.ortools.linearsolver.MPConstraint
import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
//@Test
//fun marne() {
//    val solver = SolverBuilder(MPSolver.createSolver("GLOP"))
//    val marneVectors = marneVectors()
//    val marneTargets = marneTargets()
//
//    marneVectors.map { solver.build(0.0, 80_000_000) }
//
//    marneTargets.withIndex().map { (index, target) ->
//        val values = marneVectors.map { it.find { entry -> entry.first == index }?.second ?: 0.0 }
//
////            solver.deltaedConstraint(index, target, *values.toDoubleArray())
//        solver.constraint(target, target, *values.toDoubleArray())
//
//    }
//
//    solver.objective.setMinimization()
//    solver.solver.enableOutput()
//    val out = solver.solve()
//    println(solver.deltas.map { it.name() to it.solutionValue() })
//    println(solver.constraints)
//}
class SolverBuilder(
    val solver: MPSolver,
) {
    val objective = solver.objective()

    val variables: MutableList<MPVariable> = mutableListOf()
    val deltas : MutableList<MPVariable> = mutableListOf()
    val constraints: MutableList<MPConstraint> = mutableListOf()
    operator fun get(index: Int): MPVariable {
        return variables[index]
    }
    fun build(lb: Double, ub: Double, name: String = "v_${variables.size}" ): MPVariable {
        require(lb.isFinite() || lb == -MPSolver.infinity())
        require(ub.isFinite() || ub == MPSolver.infinity())
        require(lb <= ub)
        val variable = solver.makeNumVar(lb, ub,name)
        variables.add(variable)
        return variable
    }
    fun build(lb: Number, ub: Number, name: String = "v_${variables.size}"): MPVariable {

        return build(lb.toDouble(), ub.toDouble(), name)
    }

    fun constraint(lb: Double, ub: Double, vararg values: Double): MPConstraint {
        val constraint  = solver.makeConstraint(lb, ub, "c_${constraints.size}")
        constraints.add(constraint)
        values.withIndex().filter { it.value != .0 }.forEach { (index, value) ->
            constraint.setCoefficient(get(index), value)
        }

        return constraint
    }

    fun deltaedConstraint(index: Int, target: Double, vararg values: Double) {
        val delta = build(.0, MPSolver.infinity(), "delta_$index")
        val lower = solver.makeConstraint(target, MPSolver.infinity(), "lowerdelta_$index")
        val upper = solver.makeConstraint(-MPSolver.infinity(), target, "upperdelta_$index")

        values.withIndex().filter { it.value != .0 }.forEach { (index, value) ->
            lower.setCoefficient(get(index), value)
            upper.setCoefficient(get(index), value)
        }
        lower.setCoefficient(delta, 1.0)
        upper.setCoefficient(delta, -1.0)
        objective.setCoefficient(delta, 1.0)
        deltas.add(delta)
    }


    fun objective(maximize: Boolean, vararg values: Double) {
        values.withIndex().filter { it.value != .0 }.forEach { (index, value) ->
            objective.setCoefficient(get(index), value)
        }
        if(maximize) {
            objective.setMaximization()
        } else {
            objective.setMinimization()
        }
    }
    fun solve() = solver.solve() to variables.associate { it.name() to it.solutionValue() }
}