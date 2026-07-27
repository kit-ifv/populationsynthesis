package edu.kit.ifv.populationsynthesis

import edu.kit.ifv.populationsynthesis.algorithms.ArrayScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.TargetNumberObserver
import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU
import edu.kit.ifv.populationsynthesis.algorithms.ipu.Kaczmarz
import kotlin.test.Test

class SmallIPUTest {
    // Surprisingly, IPF works even when expanded to an IPU form
    @Test
    fun testSmallExperiment() {
        val ipu = GenericIPU.legacy

        val a = ArrayScalableVector(1, 0, 1, 0)
        val b = ArrayScalableVector(1, 0, 0, 1)
        val c = ArrayScalableVector(0, 1, 1, 0)
        val d = ArrayScalableVector(0, 1, 0, 1)

        val observerA = TargetNumberObserver("First Column", 0, listOf(a, b), 90.0)
        val observerB = TargetNumberObserver("Second Column", 1, listOf(c, d), 30.0)
        val observerC = TargetNumberObserver("First Row", 2, listOf(a, c), 80.0)
        val observerD = TargetNumberObserver("Second Row", 3, listOf(b, d), 40.0)

        ipu.run(listOf(a, b, c, d,), listOf(observerA, observerB, observerC, observerD))

    }

    // Apparently this diverges. Example 3 from https://opus.bibliothek.uni-augsburg.de/opus4/frontdoor/deliver/index/docId/1229/file/mpreprint_09_005.pdf

    // Robin: Yes it diverges, and the reason is relatively simple: The 0.0 entry for c causes the linear system to
    // be only solvable with a = 4, b = -2, d = 4. Obviously -2 ain't happening in a strictly positive

    @Test
    fun testDivergenceExample() {

            val ipu = GenericIPU.legacy


            val a = ArrayScalableVector(1, 0, 1, 0)
            val b = ArrayScalableVector(1, 0, 0, 1)
            val c = ArrayScalableVector(0, 1, 1, 0)
            val d = ArrayScalableVector(0, 1, 0, 1)

            val observerA = TargetNumberObserver("First Column", 0, listOf(a, b), 2.0)
            val observerB = TargetNumberObserver("Second Column", 1, listOf(c, d), 4.0)
            val observerC = TargetNumberObserver("First Row", 2, listOf(a, c), 4.0)
            val observerD = TargetNumberObserver("Second Row", 3, listOf(b, d), 2.0)

            a.scalar = 30.0
            b.scalar = 10.0
            c.scalar = 0.0
            d.scalar = 20.0

            ipu.run(listOf(a, b, c, d,), listOf(observerA, observerB, observerC, observerD))

    }

    // Kaczmarz is a cool new implementation i found but it doesnt promise nonnegativity.
    @Test
    fun testKarzmarz() {
        val ipu = Kaczmarz

        val a = ArrayScalableVector(1, 0, 1, 0)
        val b = ArrayScalableVector(1, 0, 0, 1)
        val c = ArrayScalableVector(0, 1, 1, 0)
        val d = ArrayScalableVector(0, 1, 0, 1)

        val observerA = TargetNumberObserver("First Column", 0, listOf(a, b), 90.0)
        val observerB = TargetNumberObserver("Second Column", 1, listOf(c, d), 30.0)
        val observerC = TargetNumberObserver("First Row", 2, listOf(a, c), 80.0)
        val observerD = TargetNumberObserver("Second Row", 3, listOf(b, d), 40.0)

        ipu.run(listOf(a, b, c, d,), listOf(observerA, observerB, observerC, observerD))
        println("Done")
    }
}