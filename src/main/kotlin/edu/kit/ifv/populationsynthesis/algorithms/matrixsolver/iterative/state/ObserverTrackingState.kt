package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.state

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.SolverState

interface ObserverTrackingState: SolverState {
    val observers: List<RuleObserver>
}