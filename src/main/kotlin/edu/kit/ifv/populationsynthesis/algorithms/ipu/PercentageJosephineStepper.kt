package edu.kit.ifv.populationsynthesis.algorithms.ipu

import edu.kit.ifv.populationsynthesis.Signature
import edu.kit.ifv.populationsynthesis.algorithms.ConstantVector
import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.SignatureScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.Vector
import edu.kit.ifv.populationsynthesis.algorithms.asVector
import edu.kit.ifv.populationsynthesis.algorithms.multiplyComponent
import edu.kit.ifv.populationsynthesis.algorithms.safeComponentInverse
import edu.kit.ifv.populationsynthesis.algorithms.sum

class PercentageJosephineStepper(
    private val maxIter: Int = 10000,

    private val exitCriterion: () -> Boolean = { false },
    private val stepper: DimensionStep = DimensionStep.original,
): MatrixSolver {
    override fun solve(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ) {

        val target = observers.toTargetVector()
        val componentInverse = target.safeComponentInverse(
        )
        val scaledTarget = ConstantVector.uniform(1.0, target.size)
        val scaledVectors = vectors.map { it.multiplyComponent(componentInverse) }
        val currentLength = scaledVectors.sum().length()
        val scaling = scaledTarget.length() / currentLength
        scaledVectors.forEach { vector ->
            vector.scalar *= scaling
        }

        repeat(maxIter) {
            if(exitCriterion()) {
                return
            }

            stepper.step(scaledTarget, scaledVectors)

        }

        vectors.zip(scaledVectors).forEach { (original, scaled) ->
            original.scalar = scaled.scalar
        }

    }
    private fun Vector.multiplyWith(other: Vector): Vector {
        return ConstantVector(zip(other) { a, b ->
            a * b
        })
    }


    private fun Collection<RuleObserver>.toTargetVector(): ConstantVector = map { it.expected }.asVector()
}