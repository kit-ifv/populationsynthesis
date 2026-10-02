package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeSource

open class DefaultIterativeMatrixSolver<S : SolverState>(
    private val maxIter: Int = 1000,
    private val maxDuration: Duration = 30.minutes,
    private val step: IterationStep<S>,
    private val stateFactory: SolverStateFactory<S>,

    ) : IterativeMatrixSolver<S> {

    constructor(config: SolverConfiguration<S>) : this(
        maxIter = config.maxIter,
        maxDuration = config.maxDuration,
        step = config.step,
        stateFactory = config.stateFactory,
    )

    private val observers: MutableList<IterationObserver<S>> = mutableListOf()

    fun register(observer: IterationObserver<S>) {
        observers.add(observer)
    }

    fun deregister(observer: IterationObserver<S>) {
        observers.remove(observer)
    }

    override fun initialize(vectors: Collection<ScalableVector>, observers: Collection<RuleObserver>): S {
        val state = stateFactory.create(vectors, observers)
        notifyObservers(state = state)
        return state
    }

    override fun iteration(state: S, iteration: Int) {
        val timeMark = TimeSource.Monotonic.markNow()
        step(state, iteration)
        val iterationDuration = timeMark.elapsedNow()
        notifyObservers(iteration, iterationDuration, state)
    }

    open fun step(state: S, iteration: Int) = step.step(state, iteration)

    override fun exitCriterion(state: S, iteration: Int): Boolean {
        return iteration > maxIter || state.spawnMark.elapsedNow() > maxDuration
    }


    protected fun notifyObservers(
        iteration: Int = -1,
        duration: Duration = Duration.ZERO,
        state: S,
    ) {
        observers.forEach {
            it.observe(iteration, duration, state)
        }
    }


}


class SolverConfiguration<S : SolverState>(
    var maxIter: Int = 1000,
    var maxDuration: Duration = 30.minutes,
) {
    lateinit var step: IterationStep<S>
    lateinit var stateFactory: SolverStateFactory<S>
    fun build(): DefaultIterativeMatrixSolver<S> {
        return DefaultIterativeMatrixSolver(
            maxIter = maxIter,
            maxDuration = maxDuration,
            step = step,
            stateFactory = stateFactory,
        )
    }

    companion object {
        // Deliberately getter and not a field so that each config is "fresh"
        val ipuConfig get()= SolverConfiguration<ObserverTrackingState>().apply {
            step = IterationStep.ipuStep
            stateFactory = SolverStateFactory.ipuStateNormalized
        }

        val ccdConfig get()= SolverConfiguration<MutableResidualState>().apply {
            step = IterationStep.ccdStep
            stateFactory = SolverStateFactory.normalization
        }
    }
}

object Solvers {

    fun iterativeProportionalUpdate(lambda: SolverConfiguration<ObserverTrackingState>.() -> Unit = {}): DefaultIterativeMatrixSolver<ObserverTrackingState> {
        return create{
            step = IterationStep.ipuStep
            stateFactory = SolverStateFactory.ipuStateNormalized
            apply(lambda)
        }
    }


    fun cyclicCoordinateDescent(lambda: SolverConfiguration<MutableResidualState>.() -> Unit = {}): DefaultIterativeMatrixSolver<MutableResidualState> {
        return create {
            step = IterationStep.ccdStep
            stateFactory = SolverStateFactory.normalization
            apply(lambda)
        }
    }


    fun <S: SolverState> create(lambda: SolverConfiguration<S>.() -> Unit = {}): DefaultIterativeMatrixSolver<S> {
        val solverConfiguration = SolverConfiguration<S>().apply(lambda)
        return solverConfiguration.build()
    }

    val IPU get()= iterativeProportionalUpdate()
    val CCD get() = cyclicCoordinateDescent()
}

