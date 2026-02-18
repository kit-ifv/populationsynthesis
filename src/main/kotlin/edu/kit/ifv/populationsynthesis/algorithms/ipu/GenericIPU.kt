package edu.kit.ifv.populationsynthesis.algorithms.ipu

import edu.kit.ifv.populationsynthesis.SignatureOld
import edu.kit.ifv.populationsynthesis.Signature
import edu.kit.ifv.populationsynthesis.algorithms.IPUOutput
import edu.kit.ifv.populationsynthesis.algorithms.PerformanceLoggingIPU
import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.rules.IndexedRule
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.toScalableVectorOld
import edu.kit.ifv.populationsynthesis.toScalableVector
import edu.kit.ifv.populationsynthesis.utils.invertMap
import java.nio.file.Path

fun interface GenericIPU {

    fun run(vectors: Collection<ScalableVector>, observers: Collection<RuleObserver>)

    /**
     * THe most generic version of IPU does not care about unifying vectors. If you put in multiple identical vecotrs
     * well then thats on you. Returns the observed values and expected values.
     */
    fun <I> calculateDirect(
        vectors: Collection<ScalableVector>,
        rules: Collection<Rule<I>>,
    ): DirectRunReport {
        val (emptyVectors, nonEmptyVectors) =  vectors.partition { it.signature.isEmpty() }
        val observers = rules.withIndex().map {
            RuleObserver.fromRule(it.value, it.index, nonEmptyVectors)
        }
        run(nonEmptyVectors, observers)
        emptyVectors.forEach { vector ->
            vector.scalar = 0.0
        }
        return DirectRunReport(observers, emptyVectors.size)
    }
    fun <X, I> sigCalcAct(
        signatures: Map<SignatureOld, List<I>>,
        rules: Collection<IndexedRule<I>>,
    ): List<IPUOutput<Signature>>  {
        val vectors = signatures.keys.map { it.toScalableVector() }
        val observers = rules.map { (index, rule) ->
            RuleObserver.fromRule(rule, index, vectors)
        }

        run(vectors, observers)
        return vectors.map { IPUOutput(it.signature, it.scalar) }
    }
//    fun <I> calculateUnfiltered(elements: Collection<I>, rules: Collection<Rule<I>>): List<IPUOutput<I>> {
//        val vectorMapping = elements.associateWith { rules.toScalableVectorOld(it) }
//        calculateDirect(vectorMapping.values, rules)
//        return vectorMapping.map { (k, v) ->
//            IPUOutput(k, v.scalar)
//        }
//    }

//    fun <I> calculate(
//        elements: Collection<I>,
//        rules: Collection<Rule<I>>,
//    ): List<IPUOutput<List<I>>> {
//        return internalGroupedCalculation(elements, rules) {
//            it.map { (k, v) ->
//                IPUOutput(v, k.scalar)
//            }
//        }
//    }

    fun <I> calculateSignature(
        elements: Collection<I>,
        rules: Collection<Rule<I>>,
        ipuCalculationCallback: (DirectRunReport) -> Unit = {},
    ): List<IPUOutput<Signature>> {
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
                IPUOutput(it.signature, it.scalar)
            }
        }
    }



    private fun <X, I> internalGroupedCalculation(
        elements: Collection<I>,
        rules: Collection<Rule<I>>,
        ipuCalculationCallback: (DirectRunReport) -> Unit = {},
        resultConverter: (Map<ScalableVector, List<I>>) -> X
    ): X {
        val vectorMapping = elements.associateWith { rules.toScalableVectorOld(it) }
        val inverseMap = vectorMapping.invertMap()
        val uniqueVectors = inverseMap.toVectors()


        val errors = calculateDirect(uniqueVectors, rules)
        ipuCalculationCallback(errors)
        return resultConverter(inverseMap)
    }

    private fun <I> Map<ScalableVector, List<I>>.toVectors() : List<ScalableVector> {
        return entries.map { (scalableVector, associatedElements) ->
            scalableVector.apply {
//                scalar = associatedElements.size.toDouble()
            }

        }
    }

    fun withLogging(path: Path) = PerformanceLoggingIPU(this, path)

    companion object {
        /**
         * The original algorithm of hierarchical IPU using no external interrupt criterion.
         */
        @Suppress("MagicNumber")
        val legacy = CyclicIPU(1000)

        @Suppress("MagicNumber")
        val newAlgorithm = GenericIPU { vectors, observers ->
            var counter = 0

            while (observers.maxOf { it.quotientDifference } >= 1.001 && counter < 1000) {
                val sorted = observers.sortedByDescending { it.quotientDifference }
                sorted.forEach {
                    it.optimize()
                }
                counter++
            }
        }

        val tabooList = GenericIPU { vectors, observers ->

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
        val limitOptimization = GenericIPU { vectors, observers ->
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

        val functionalTabooList = GenericIPU { vectors, observers ->

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

        val aggressiveStomping = GenericIPU { vectors, observers ->

            var counter = 0
            while (counter < 1000 * observers.size) {

                val best = observers.maxBy { it.absoluteDifference }
                best.optimize()

                counter++

            }
        }
    }
}