package edu.kit.ifv.populationsynthesis.synthesis

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.datastructures.linalg.SparseVector
import edu.kit.ifv.populationsynthesis.rules.LogicIndexer
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider
import edu.kit.ifv.populationsynthesis.rules.toRuleSet

/**
 * When the task is based on a rule provider and corresponding seed elements, then we can create the matrix task
 * from the elements by grouping them based on their representative signature (read sparse vector). Two elements
 * with the same vector can be represented by one - reduces problem size.
 *
 * Vectors depend on the ruleprovider.
 * Also the fused Problem is basically the collection of all registered rules grouped into one (but that is not that
 * important right now)
 */
open class ResourceTaskProvider<AREA, T>(
    private val seedElements: Collection<T>,
    open val ruleProvider: RuleProvider<AREA, in T>,
    val scalarInitializer: InitializeVectorScalars<T> = InitializeVectorScalars.byNormalizedElementCount
): MatrixTaskProvider<AREA, T> {
    protected val logicIndexer by lazy {  LogicIndexer.fromProvider(ruleProvider) }
    override fun provideProblemFor(area: AREA): MatrixTask {
        val rules = ruleProvider[area]
        if (rules.isEmpty()) return MatrixTask.EMPTY
        return createTask(rules)
    }

    protected fun createTask(rules: RuleSet<T>): MatrixTask {
        val entries = seedElements.groupBy { logicIndexer.createSignature(it) } as Map<SparseVector, List<T>>
        val vectors = scalarInitializer.generateScalars(entries)
        val observers = logicIndexer.indexRules(rules).map { (index, rule) ->
            RuleObserver.fromRule(rule, index, vectors)
        }
        return MatrixTask(vectors, observers)
    }

    fun fusedProblem(): MatrixTask {
        val rules = ruleProvider.getAllRules()
        val areas = rules.keys
        val allLogics = logicIndexer.logics
        val largeRuleSet = allLogics.map { ruleProvider.getSum(areas, it) }.toRuleSet()
        return createTask(largeRuleSet)
    }
}