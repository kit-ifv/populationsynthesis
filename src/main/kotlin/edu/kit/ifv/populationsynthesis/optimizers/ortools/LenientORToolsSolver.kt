package edu.kit.ifv.populationsynthesis.optimizers.ortools

import com.google.ortools.Loader
import com.google.ortools.linearsolver.MPObjective
import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import kotlin.time.Duration.Companion.minutes

open class LenientORToolsSolver(
    solver: MPSolver = MPSolver.createSolver("GLOP"),
    lowerBound: Double = 0.0,
    upperBound: Double = 80_000_000.0,
): ORToolsSolver(solver, lowerBound, upperBound) {
    protected val objective: MPObjective = solver.objective()
    override fun addObserver(observer: RuleObserver) {
        val target = observer.expected
        val rowIndex = observer.observedIndex
        val delta = solver.makeNumVar(.0, MPSolver.infinity(), "delta_$rowIndex")
        val lower = solver.makeConstraint(target, MPSolver.infinity(), "lowerdelta_$rowIndex")
        val upper = solver.makeConstraint(-MPSolver.infinity(), target, "upperdelta_$rowIndex")
        observer.vectors.forEach{
            val variable = vectorVariables.getValue(it)
            val coefficient = it.baseVector[rowIndex]
            lower.setCoefficient(variable, coefficient)
            upper.setCoefficient(variable, coefficient)
        }
        lower.setCoefficient(delta, 1.0)
        upper.setCoefficient(delta, -1.0)
        objective.setCoefficient(delta, 1.0 )
    }

    companion object {
        init {
            Loader.loadNativeLibraries()
        }
    }
}


class WeightRegardingLenientORToolsSolver(
    solver: MPSolver = MPSolver.createSolver("GLOP"),
    lowerBound: Double = 0.0,
    upperBound: Double = 80_000_000.0,
    private val lambda: Double = 1.0,

): LenientORToolsSolver(solver, lowerBound, upperBound) {

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
            val coefficient = it.baseVector[rowIndex]
            lower.setCoefficient(variable, coefficient)
            upper.setCoefficient(variable, coefficient)
        }
        lower.setCoefficient(delta, 1.0)
        upper.setCoefficient(delta, -1.0)
        objective.setCoefficient(delta, 1.0 / target.coerceAtLeast(1.0))
//        objective.setCoefficient(delta, 1.0 / target)
    }
    lateinit var originalDistribution: Map<out Vector, Double>

    override fun loadOriginalDistribution(originalDistribution: Map<out Vector, Double>) {
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
                originalDistribution[vector]?: .0.also {
                    println("DId not find $vector")
                },
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



class LimitedLenientORToolsSolver(
    solver: MPSolver = MPSolver.createSolver("GLOP"),
    val totalTarget: Double,
    lowerBound: Double = .0,
    upperBound: Double = 80_000_000.0,
    private val lambda: Double = 1.0,
): LenientORToolsSolver(solver, lowerBound, upperBound) {
    lateinit var originalDistribution: Map<out Vector, Double>

    override fun loadOriginalDistribution(originalDistribution: Map<out Vector, Double>) {
        this.originalDistribution = originalDistribution
    }

    override fun addObserver(observer: RuleObserver) {
        val target = observer.expected
        val rowIndex = observer.observedIndex
        val delta = solver.makeNumVar(.0, MPSolver.infinity(), "delta_$rowIndex")
        val lower = solver.makeConstraint(target, MPSolver.infinity(), "lowerdelta_$rowIndex")
        val upper = solver.makeConstraint(-MPSolver.infinity(), target, "upperdelta_$rowIndex")
        observer.vectors.forEach{
            val variable = vectorVariables.getValue(it)
            val coefficient = it.baseVector[rowIndex]
            lower.setCoefficient(variable, coefficient)
            upper.setCoefficient(variable, coefficient)
        }
        lower.setCoefficient(delta, 1.0)
        upper.setCoefficient(delta, -1.0)
        objective.setCoefficient(delta, 1.0)
//        objective.setCoefficient(delta, 1.0 / target)
    }

    override fun afterConfigHook() {

        vectorVariables.forEach { (vector, variable) ->
            addDeviationConstraints(
                variable,
                originalDistribution[vector]?: .0.also {
                    println("DId not find $vector")
                },
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
        val bigA = totalTarget * originalShare
        val upper = solver.makeConstraint(
            -MPSolver.infinity(),
            bigA,
            "upper_${delta.name()}",
        )

        upper.setCoefficient(variable, 1.0)
        upper.setCoefficient(delta, -1.0)

        val lower = solver.makeConstraint(
            -MPSolver.infinity(),
            -bigA,
            "lower_${delta.name()}",
        )
        lower.setCoefficient(variable, -1.0)
        lower.setCoefficient(delta, -1.0)


        objective.setCoefficient(delta, weight)
    }

    override fun solve(): Pair<Map<Vector, Double>, Result> {
        solver.setTimeLimit(10.minutes.inWholeMilliseconds)
        val output =  super.solve()
        return output
    }
}

class BoundedLenientORToolsSolver(
    solver: MPSolver = MPSolver.createSolver("GLOP"),
    val totalTarget: Double,
    val boundsDeterminer: BoundsDeterminer,
    lowerBound: Double = .0,
    upperBound: Double = 80_000_000.0,
    private val lambda: Double = 1.0,
): LenientORToolsSolver(solver, lowerBound, upperBound)   {
    lateinit var originalDistribution: Map<out Vector, Double>

    override fun loadOriginalDistribution(originalDistribution: Map<out Vector, Double>) {
        this.originalDistribution = originalDistribution
    }

    override fun afterConfigHook() {

        vectorVariables.forEach { (vector, variable) ->
            val d = originalDistribution[vector] ?: .0.also {
                println("DId not find $vector")
            }
            variable.setLb((boundsDeterminer.lowerBound(d) * totalTarget).coerceAtLeast(.0))
            variable.setUb(boundsDeterminer.upperBound(d) * totalTarget)
        }

    }
}

interface BoundsDeterminer {
    fun lowerBound(originalShare: Double): Double
    fun upperBound(originalShare: Double): Double
}

class RelativeBoundsDeterminer(val allowedDeviation: Double) : BoundsDeterminer {
    init {
        require(allowedDeviation > 1.0) { "allowedDeviation must be larger than 1.0" }
    }
    override fun lowerBound(originalShare: Double): Double {
        return originalShare / allowedDeviation
    }

    override fun upperBound(originalShare: Double): Double {
        return originalShare * allowedDeviation
    }
}
