package edu.kit.ifv.populationsynthesis.optimizers.ortools

import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import edu.kit.ifv.populationsynthesis.optimizers.LoadMatrixProblemToSolver

abstract class ORToolsSolver protected constructor(
    val solver: MPSolver = MPSolver.createSolver("GLOP"),
    protected val upperBound: Double = 80_000_000.0,
): LoadMatrixProblemToSolver<ORToolsSolver.Result> {
    inner class Result()
    protected val vectorVariables: MutableMap<Vector, MPVariable> = mutableMapOf()
    override fun addVectorVariable(vector: Vector) {
        val v = solver.makeNumVar(.0, upperBound, "v_${vectorVariables.keys.size}")
        vectorVariables[vector] = v
    }


    override fun solve(): Pair<Map<Vector, Double>, Result> {
        solver.solve()
        return vectorVariables.mapValues { it.value.solutionValue() } to Result()
    }

    fun isFeasible(): Boolean {
        val status =  solver.solve()
        return status !in setOf(MPSolver.ResultStatus.INFEASIBLE, MPSolver.ResultStatus.MODEL_INVALID, MPSolver.ResultStatus.ABNORMAL)
    }

}