package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver

import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative.Solvers
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.synthesis.MatrixTask

fun interface MatrixSolver {

    fun solve(vectors: Collection<ScalableVector>, observers: Collection<RuleObserver>)
    fun solve(matrixTask: MatrixTask) = solve(matrixTask.vectors, matrixTask.observers)


    companion object {
        /**
         * The original algorithm of hierarchical IPU using no external interrupt criterion.
         */
        @Suppress("MagicNumber")
        val legacy = Solvers.IPU


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