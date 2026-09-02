package edu.kit.ifv.populationsynthesis.algorithms.ipu

import edu.kit.ifv.populationsynthesis.datastructures.linalg.ArrayScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.TargetNumberObserver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.JosephineStepper
import kotlin.test.Test

class JosephineStepperTest {
    @Test
    fun josephineStepper() {
        val a = ArrayScalableVector(doubleArrayOf(1.0, 1.0), 1.0)
        val b = ArrayScalableVector(doubleArrayOf(1.0, 3.0), 1.0)

        val obsA = TargetNumberObserver("First", 0, listOf(a, b), 4.0)
        val obsB = TargetNumberObserver("Second", 1, listOf(a, b), 6.0)
        val j = JosephineStepper()
        j.solve(listOf(a, b), listOf(obsA, obsB))


    }

}