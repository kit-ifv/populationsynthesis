package edu.kit.ifv.populationsynthesis.algorithms.sampling

import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.utils.cycleToSizeAndShuffle

/**
 * A sampler on the concrete type of ScalableVectors can simply read the scalar field from the vector, as that
 * field is representative of how many elements should be sampled.
 */
fun interface ScalableVectorSampler<H> : GroupedSampler<ScalableVector, H> {
    override fun Map<ScalableVector, List<H>>.sample(): List<H>

    companion object {


        fun <T, H> coerceMaintainingOrder(): ScalableVectorSampler<H> {
            return ScalableVectorSampler {
                entries.flatMap {
                    it.value.cycleToSizeAndShuffle(it.key.scalar.toInt())
                }
            }
        }
    }
}