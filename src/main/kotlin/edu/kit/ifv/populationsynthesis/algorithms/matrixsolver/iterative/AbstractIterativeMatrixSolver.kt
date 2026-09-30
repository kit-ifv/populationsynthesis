package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeSource

abstract class AbstractIterativeMatrixSolver<S: SolverState>(
    private val maxIter: Int = 1000,
    private val maxDuration: Duration = 30.minutes,
    private val stateFactory: SolverStateFactory<S>,

    ): IterativeMatrixSolver<S> {
    private val observers: MutableList<IterationObserver<S>> = mutableListOf()

    fun register(observer: IterationObserver<S>) {
        observers.add(observer)
    }
    fun deregister(observer: IterationObserver<S>) {
        observers.remove(observer)
    }

    override fun initialize(vectors: Collection<ScalableVector>, observers: Collection<RuleObserver>): S {
        val state = stateFactory.create(vectors.map { it.toScaledVector() }, observers)
        notifyObservers(state = state)
        return state
    }
    override fun iteration(state: S, iteration: Int) {
        val timeMark = TimeSource.Monotonic.markNow()
        step(state, iteration)
        val iterationDuration = timeMark.elapsedNow()
        notifyObservers(iteration, iterationDuration, state)
    }
    abstract fun step(state: S, iteration: Int)

    override fun exitCriterion(state: S, iteration: Int): Boolean {
        return iteration > maxIter || state.spawnMark.elapsedNow() > maxDuration
    }

    protected fun notifyObservers(
        iteration: Int = -1,
        duration: Duration = Duration.Companion.ZERO,
        state: S,
    ) {
        observers.forEach {
            it.observe(iteration, duration, state)
        }
    }
}