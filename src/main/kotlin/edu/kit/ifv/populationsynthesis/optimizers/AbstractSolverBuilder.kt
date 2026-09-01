package edu.kit.ifv.populationsynthesis.optimizers


import com.google.ortools.Loader
import com.google.ortools.linearsolver.MPSolver
import com.google.ortools.linearsolver.MPVariable
import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.Vector
import edu.kit.ifv.populationsynthesis.algorithms.WeightedElement
import edu.kit.ifv.populationsynthesis.algorithms.ipu.MatrixSolver
import org.ejml.data.Matrix
import org.ojalgo.optimisation.ExpressionsBasedModel

typealias OJVariable = org.ojalgo.optimisation.Variable
typealias OJExpression =  org.ojalgo.optimisation.Expression

interface ExpressionFunctions<E> {
    val original: E
    var lowerBound: Double
    var upperBound: Double

}

class OJExpressionWrapper(
    override val original: OJExpression,
): ExpressionFunctions<OJExpression> {
    override var lowerBound: Double
    get() = original.lowerLimit.toDouble()
    set(value) {
        original.lower(value)
    }
    override var upperBound: Double
        get() = original.upperLimit.toDouble()
        set(value) {
            original.upper(value)
        }
}

abstract class AbstractSolverBuilder<M,E, V>(
    private val model: M,
    private val expressionSpawner: M.(String) -> E,
    private val variableSpawner: M.(String) -> V
) where E: ExpressionFunctions<*> {


    fun addVariable(variableName: String): V {
        return model.variableSpawner(variableName)
    }

    fun addExpression(name: String): E {
        return model.expressionSpawner(name)
    }

    private val registeredVectors: MutableMap<Vector, V> = mutableMapOf()
    fun addVectorVariable(name: String, vector: Vector): V {
        val v = addVariable(name)
        registeredVectors[vector] = v
        return v
    }

    fun addObserverRow(observer: RuleObserver) {
        val target = observer.expected
        require(observer.vectors.all { it in registeredVectors.keys }) { "All vectors need to be registered" }
        val expression = addExpression("lower_${observer.observedIndex}")
        expression.lowerBound = target

    }

}

class OptimizationSolver<T>(
    private val model: LoadVectors<T>
): MatrixSolver {
    override fun solve(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ) {
        vectors.forEach { vector ->
            model.addVectorVariable(vector)
        }
        observers.forEach { observer ->
            model.addObserver(observer)
        }
        val (resultLookup, bonus) = model.solve()
        vectors.forEach { vector ->
            vector.scalar = resultLookup[vector] ?: .0.also {
                println("Could not find scalar for vector $vector")
            }
        }
    }

}


interface LoadVectors<EXTRA> {
    fun addVectorVariable(vector: Vector)
    fun addObserver(observer: RuleObserver)
    fun solve(): Pair<Map<Vector, Double>, EXTRA>
}


class OJSolverBuilder(
    private val model: ExpressionsBasedModel = ExpressionsBasedModel()
): LoadVectors<OJSolverBuilder.Result> {
    inner class Result(
        val deltas: List<OJVariable>
    )
    private val objective = model.addExpression("squared_deviation").weight(1.0)
    private val vectorVariables: MutableMap<Vector, OJVariable> = mutableMapOf()
    private val deltaVariables: MutableList<OJVariable> = mutableListOf()
    override fun addVectorVariable(vector: Vector) {
        val v = model.addVariable("v_${vectorVariables.keys.size}").lower(.0)
        vectorVariables[vector] = v
    }

    override fun addObserver(observer: RuleObserver) {
        val target = observer.expected
        val rowIndex = observer.observedIndex
        val delta = model.addVariable("delta_$rowIndex").lower(.0)
        deltaVariables.add(delta)
        require(observer.vectors.all { it in vectorVariables.keys }) { "All vectors need to be registered" }
        val lower = model.addExpression("lower_${rowIndex}").lower(target)
        val upper = model.addExpression("upper_${rowIndex}").upper(target)

        observer.vectors.forEach{
            val coefficient = it.attributeForIndex(rowIndex)
            require(coefficient != .0) {"A coefficient of .0 should never be registered in an observer"}
            val variable = vectorVariables.getValue(it)
            lower.set(variable, coefficient)
            upper.set(variable, coefficient)
        }
        lower.set(delta, 1.0)
        upper.set(delta, -1.0)

        objective.set(delta, delta, 1.0)
    }
    override fun solve(): Pair<Map<Vector, Double>, OJSolverBuilder.Result> {
        model.minimise()
        return vectorVariables.mapValues { it.value.value.toDouble() } to Result(deltaVariables)
    }
}

data class VariableBounds(
    val name: String,
    val lowerBound: Double,
    val upperBound: Double,
)

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

object ORSolverFactory {
    init {
        Loader.loadNativeLibraries()
    }
    fun lenient(lambda: ORFactoryConfig.() -> Unit): ORToolsSolver {
        val config = ORFactoryConfig().also(lambda)
        return LenientORToolsSolver(config.solver, config.upperBound)
    }
    fun direct(lambda: ORFactoryConfig.() -> Unit): ORToolsSolver {
        val config = ORFactoryConfig().also(lambda)
        return DirectORToolsSolver(config.solver, config.upperBound)
    }
    val direct  by lazy {direct {}}
    val lenient by lazy {lenient {}}

}

data class ORFactoryConfig(
    var solver: MPSolver = MPSolver.createSolver("GLOP"),
    var upperBound: Double = 80_000_000.0,
)

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

abstract class ORToolsSolver(
    val solver: MPSolver = MPSolver.createSolver("GLOP"),
    protected val upperBound: Double = 80_000_000.0,
): LoadVectors<ORToolsSolver.Result> {
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