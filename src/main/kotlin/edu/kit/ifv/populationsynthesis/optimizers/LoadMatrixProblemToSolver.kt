package edu.kit.ifv.populationsynthesis.optimizers

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector

/**
 * I need the following functions to code an optimization problem solver onto my existing data structures for
 * initial population solving.
 */
interface LoadMatrixProblemToSolver<EXTRA> {
    fun addVectorVariable(vector: Vector)
    fun addObserver(observer: RuleObserver)
    fun solve(): Pair<Map<Vector, Double>, EXTRA>
}