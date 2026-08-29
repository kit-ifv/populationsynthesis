package edu.kit.ifv.populationsynthesis.algorithms.ipu

import edu.kit.ifv.populationsynthesis.algorithms.ConstantVector
import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.Vector
import edu.kit.ifv.populationsynthesis.algorithms.asVector
import edu.kit.ifv.populationsynthesis.algorithms.sum


fun interface JStep {
    fun step(target: Vector, vectors: Collection<ScalableVector>)

    companion object {
        val original: JStep = JStep { target, vectors ->
            val difference = target - vectors.sum()
            val dLength = difference.length()
            vectors.forEach { vector ->
                vector.scalar *= 1 + (vector.cosineSimilarity(difference) * dLength)
            }

            val scalingFactor = target.length() / vectors.sum().length()
            vectors.forEach { vector ->
                vector.scalar *= scalingFactor
            }
        }

        val gradientDescent: JStep = JStep { target, vectors ->

        }
    }
}

class JosephineStepper(
    private val maxIter: Int = 20,

    private val exitCriterion: () -> Boolean = { false },
    private val stepper: JStep = JStep.original,
): MatrixSolver {
    override fun solve(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ) {

        val target = observers.toTargetVector()

        repeat(maxIter) {
            if(exitCriterion()) {
                return
            }

            stepper.step(target, vectors)


        }

    }

    private fun Collection<RuleObserver>.toTargetVector(): ConstantVector = map { it.expected }.asVector()
}