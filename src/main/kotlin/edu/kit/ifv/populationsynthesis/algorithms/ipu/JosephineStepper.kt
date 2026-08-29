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
            println("Taking Step: $dLength")
            vectors.forEach { vector ->
                val tempScale = (vector.cosineSimilarity(difference) * dLength / vectors.size * 2)
                val scaling = 1 + tempScale
                vector.scalar *= scaling
            }

            val scalingFactor = target.length() / vectors.sum().length()
            vectors.forEach { vector ->
                vector.scalar *= scalingFactor
            }
            println("Point is ${vectors.sum().map { it }.toString()}")
            println("Targe is ${target}")

            println("Took a Step: ${(target - vectors.sum()).length()}")
        }
        // We have the sphere, the length of v (invariant) etc. each
        val otherIdea: JStep = JStep { target, vectors ->
            val difference = target - vectors.sum()
            val dLength = difference.length()
            println("Taking Step: $dLength")
            vectors.forEach { vector ->
                val tempScale = (vector.cosineSimilarity(difference) * dLength / vector.length() / 2 / vectors.size)
                vector.scalar *= tempScale
            }

//            val scalingFactor = target.length() / vectors.sum().length()
//            vectors.forEach { vector ->
//                vector.scalar *= scalingFactor
//            }
            println("Point is ${vectors.sum().map { it }.toString()}")
            println("Targe is ${target}")

            println("Took a Step: ${(target - vectors.sum()).length()}")
        }
        val gradientDescent: JStep = JStep { target, vectors ->

        }
    }
}

class JosephineStepper(
    private val maxIter: Int = 10000,

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