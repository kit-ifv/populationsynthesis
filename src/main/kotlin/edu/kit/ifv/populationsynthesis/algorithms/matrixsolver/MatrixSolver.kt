package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver

import edu.kit.ifv.populationsynthesis.datastructures.Signature
import edu.kit.ifv.populationsynthesis.algorithms.*
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.ipu.CyclicIPU
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.SparseScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.isRelevantFor
import edu.kit.ifv.populationsynthesis.rules.IndexedRule
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.toScalableVector
import edu.kit.ifv.populationsynthesis.utils.invertMap
import java.nio.file.Path

fun interface MatrixSolver {

    fun solve(vectors: Collection<ScalableVector>, observers: Collection<RuleObserver>)

    /**
     * THe most generic version of IPU does not care about unifying vectors. If you put in multiple identical vecotrs
     * well then thats on you. Returns the observed values and expected values.
     */
    fun <I> calculateDirect(
        vectors: Collection<ScalableVector>,
        rules: Collection<Rule<I>>,
    ): DirectRunReport {
        val (emptyVectors, nonEmptyVectors) = vectors.partition { it.isEmpty() }
        val observers = rules.withIndex().map {
            RuleObserver.fromRule(it.value, it.index, nonEmptyVectors)
        }
        solve(nonEmptyVectors, observers)
        emptyVectors.forEach { vector ->
            vector.scalar = 0.0
        }
        return DirectRunReport(observers, emptyVectors.size)
    }

    fun <I> sigCalcAct(
        signatures: Map<Signature, List<I>>,
        rules: Collection<IndexedRule<I>>,
        initialVectorCallback: (List<ScalableVector>) -> Unit,
        solutionCallback: (List<ScalableVector>, List<RuleObserver>) -> Unit = { _, _ ->

        }
    ): List<WeightedElement<Signature>> {



        val vectors = signatures.entries.map { (k, v) ->
            val totalSize = signatures.values.sumOf { it.size }
            SparseScalableVector(k, v.size.toDouble() / totalSize)
//            ArrayScalableVector(k, v.size.toDouble() / totalSize)
        }
        initialVectorCallback(vectors)
        val relevantIndices = rules.map { it.index }.toSet()
        val (relevantVectors, irrelevantVectors) = vectors.partition { it.isRelevantFor(relevantIndices) }
        val observers = rules.map { (index, rule) ->
            RuleObserver.fromRule(rule, index, relevantVectors)
        }
        solve(relevantVectors, observers)
        irrelevantVectors.forEach { vector ->
            vector.scalar = 0.0
        }
        solutionCallback(vectors, observers)
        return vectors.map { WeightedElement(it.baseVector, it.scalar) }
    }


    fun <I> calculateSignature(
        elements: Collection<I>,
        rules: Collection<Rule<I>>,
        ipuCalculationCallback: (DirectRunReport) -> Unit = {},
    ): List<WeightedElement<Signature>> {
        return internalGroupedCalculation(elements, rules, ipuCalculationCallback) {
//            val expectedAmounts = it.keys.sumOf { it.scalar }
//            val collectedAmounts = it.values.sumOf { it.size }
//            val plannedDelta = expectedAmounts / collectedAmounts
//            val shifts = it.entries.associate { (k, v) ->
//                k to Pair(k.scalar / expectedAmounts, (v.size.toDouble() / collectedAmounts))
//            }
//
//
//            val df = mapOf(
//                "actuals" to it.entries.map { (k, v) -> k.scalar / expectedAmounts },
//                "expecteds" to it.entries.map { (k, v) -> (v.size.toDouble() / collectedAmounts) },
//
//            )
//            plotLogHistograms(df)

            it.keys.map {
                WeightedElement(it.baseVector, it.scalar)
            }
        }
    }


    private fun <X, I> internalGroupedCalculation(
        elements: Collection<I>,
        rules: Collection<Rule<I>>,
        ipuCalculationCallback: (DirectRunReport) -> Unit = {},
        resultConverter: (Map<SparseScalableVector, List<I>>) -> X,
    ): X {
        val vectorMapping = elements.associateWith { rules.toScalableVector(it) }
        val inverseMap = vectorMapping.invertMap()
        val uniqueVectors = inverseMap.toVectors()


        val errors = calculateDirect(uniqueVectors, rules)
        ipuCalculationCallback(errors)
        return resultConverter(inverseMap)
    }

    private fun <I> Map<SparseScalableVector, List<I>>.toVectors(): List<SparseScalableVector> {
        return entries.map { (scalableVector, associatedElements) ->
            scalableVector.apply {
//                scalar = associatedElements.size.toDouble()
            }

        }
    }


    companion object {
        /**
         * The original algorithm of hierarchical IPU using no external interrupt criterion.
         */
        @Suppress("MagicNumber")
        val legacy = CyclicIPU(1000)


        @Suppress("MagicNumber")
        val newAlgorithm = MatrixSolver { vectors, observers ->
            var counter = 0

            while (observers.maxOf { it.quotientDifference } >= 1.001 && counter < 1000) {
                val sorted = observers.sortedByDescending { it.quotientDifference }
                sorted.forEach {
                    it.optimize()
                }
                counter++
            }
        }

        val tabooList = MatrixSolver { vectors, observers ->

            var counter = 0
            while (counter < 1000) {
                val observerCopy = observers.toMutableList()
                while (observerCopy.isNotEmpty() && counter < 1000) {
                    val best = observers.maxBy { it.absoluteDifference }
                    best.optimize()
                    observerCopy.remove(best)
                    counter++
                }


            }
        }
        val limitOptimization = MatrixSolver { vectors, observers ->
            val indexedObservers = observers.withIndex().toMutableList()
            val counters = indexedObservers.map { 100 }.toIntArray()
            while (indexedObservers.size >= 2) {
                val target = indexedObservers.maxBy { (i, observer) -> observer.absoluteDifference }
                val (idx, candidate) = target
                candidate.optimize()
                counters[idx]--

                if (counters[idx] <= 0) {
                    indexedObservers.remove(target)
                }
            }
            val worst = observers.maxBy { it.absoluteDifference }
            worst.optimize()
        }

        val functionalTabooList = MatrixSolver { vectors, observers ->

            var counter = 0
            while (counter < 1000) {
                val observerCopy = observers.toMutableList()
                while (observerCopy.isNotEmpty() && counter < 1000) {
                    val best = observerCopy.maxBy { it.absoluteDifference }
                    best.optimize()
                    observerCopy.remove(best)
                    counter++
                }


            }
        }

        val aggressiveStomping = MatrixSolver { vectors, observers ->

            var counter = 0
            while (counter < 1000 * observers.size) {

                val best = observers.maxBy { it.absoluteDifference }
                best.optimize()

                counter++

            }
        }
    }
}