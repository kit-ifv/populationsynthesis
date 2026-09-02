package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver


import edu.kit.ifv.populationsynthesis.datastructures.linalg.ConstantVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.Vector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.asVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.multiplyComponent
import edu.kit.ifv.populationsynthesis.datastructures.linalg.normalize
import edu.kit.ifv.populationsynthesis.datastructures.linalg.safeComponentInverse
import edu.kit.ifv.populationsynthesis.datastructures.linalg.sum
import edu.kit.ifv.populationsynthesis.datastructures.linalg.toSignatureVector
import kotlin.math.max


fun interface DimensionStep {
    fun step(target: Vector, vectors: Collection<ScalableVector>)

    companion object {
        val original: DimensionStep = DimensionStep { target, vectors ->
            val difference = target - vectors.sum()
            val dLength = difference.length()
            println(difference)
            vectors.forEach { vector ->
                val tempScale = (vector.safeCosineSimilarity(difference) * dLength / vectors.size * 2)
                val scaling = 1 + tempScale
                vector.scalar *= scaling
            }

            val scalingFactor = target.length() / vectors.sum().length()
            vectors.forEach { vector ->
                vector.scalar *= scalingFactor
            }
//            println("Point is ${vectors.sum().map { it }.toString()}")
//            println("Targe is ${target}")
//
//            println("Took a Step: ${(target - vectors.sum()).length()}")
        }
        // We have the sphere, the length of v (invariant) etc. each
        val additiveNonnegativeGradientDescent: DimensionStep = DimensionStep { target, vectors ->
            val p = vectors.sum()
            val difference = target - p
            val dLength = difference.length()

            vectors.forEach { vector ->
                var update = .0
                val cosineSimilarity = vector.cosineSimilarity(difference)
                if(cosineSimilarity.isFinite()) {


                    val tempScale = (cosineSimilarity * dLength / 2.0 / vectors.size)

                    update = max(.0, vector.scalar + tempScale)
                }
                vector.scalar = update
            }

            println(vectors.sum())
        }
    }
}

interface VectorTransformations {
    fun targetVectorTransformation(original: Vector): Vector
    fun componentVectorTransformation(target: Vector, originals: List<ScalableVector>): List<ScalableVector>
    fun backtransformVector(original: ScalableVector, operative: ScalableVector)

    companion object {
        val NONE = object : VectorTransformations {
            override fun targetVectorTransformation(original: Vector): Vector {
                return original
            }

            override fun componentVectorTransformation(
                target: Vector,
                originals: List<ScalableVector>
            ): List<ScalableVector> {
                return originals
            }

            override fun backtransformVector(
                original: ScalableVector,
                operative: ScalableVector
            ) {
                original.scalar = operative.scalar
            }
        }

        val PERCENTAGE_TRANSFORMATION = object : VectorTransformations {
            override fun targetVectorTransformation(original: Vector): Vector {
                return ConstantVector.uniform(1.0, original.size)
            }

            override fun componentVectorTransformation(
                target: Vector,
                originals: List<ScalableVector>
            ): List<ScalableVector> {
                val componentInverse = target.safeComponentInverse()
                return originals.map { it.multiplyComponent(componentInverse) }
            }

            override fun backtransformVector(
                original: ScalableVector,
                operative: ScalableVector
            ) {
                original.scalar = operative.scalar
            }
        }

        val NORMALIZE_ELEMENT_VECTORS = object : VectorTransformations {
            override fun targetVectorTransformation(original: Vector): ScalableVector {
                return original.toSignatureVector()
            }

            override fun componentVectorTransformation(
                target: Vector,
                originals: List<ScalableVector>
            ): List<ScalableVector> {
                return originals.map { it.normalize() }
            }

            override fun backtransformVector(
                original: ScalableVector,
                operative: ScalableVector
            ) {
                original.scalar = operative.scalar / original.attributeLength()
            }
        }
    }
}



class JosephineStepper(
    private val maxIter: Int = 1000,

    private val exitCriterion: () -> Boolean = { false },
    private val vectorTransformations: VectorTransformations = VectorTransformations.NONE,
    private val stepper: DimensionStep = DimensionStep.original,
): MatrixSolver {
    override fun solve(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ) {

        val target = observers.toTargetVector()
        val transformedTarget = vectorTransformations.targetVectorTransformation(target)

        val transformedVectors = vectorTransformations.componentVectorTransformation(target, vectors.toList())
        solveInternal(transformedTarget, transformedVectors)

        vectors.zip(transformedVectors).forEach { (a, b) ->
            vectorTransformations.backtransformVector(a, b)
        }
    }

    private fun solveInternal(target: Vector, operativeVectors: List<ScalableVector>) {
        val currentLength = operativeVectors.sum().length()
        val scaling = target.length() / currentLength
        operativeVectors.forEach { vector ->
            vector.scalar *= scaling
        }
        repeat(maxIter) {
            if(exitCriterion()) {
                return
            }

            stepper.step(target, operativeVectors)

        }
    }

    private fun Collection<RuleObserver>.toTargetVector(): ConstantVector = map { it.expected }.asVector()
}

