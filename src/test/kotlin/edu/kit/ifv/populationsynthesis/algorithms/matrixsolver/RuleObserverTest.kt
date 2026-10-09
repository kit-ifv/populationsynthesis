package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver

import edu.kit.ifv.populationsynthesis.datastructures.linalg.SparseVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.sum
import edu.kit.ifv.populationsynthesis.datastructures.linalg.toList
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test
import kotlin.test.assertContentEquals

class RuleObserverTest {

    @Test
    fun vectorization() {
        val vectorA = SparseVector.fromValues(1, 0, 1)
        val vectorB = SparseVector.fromValues(1, 0, 1)
        val observerA = TargetNumberObserver("A", 0, emptyList(), expected = 10.0)
        val observerB = TargetNumberObserver("B", 2, emptyList(), expected = 10.0)
        val target = listOf(observerA, observerB).toVector()
        val a = vectorA.toScalableVector(5.0)
        val b = vectorB.toScalableVector(5.0)
        assertContentEquals(listOf(0.0, 0.0 ,.0), (target - listOf(a, b).sum()).toList())
        assertContentEquals(listOf(10.0, 0.0, 10.0), target.toList())
    }

}