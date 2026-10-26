package edu.kit.ifv.populationsynthesis.algorithms.sampling

import edu.kit.ifv.populationsynthesis.algorithms.rounding.RoundingStrategy
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import kotlin.random.Random

/**
 * This generation method tracks the amount of leftover decimals and adds one additional synthesis household once
 * a spillover occurs.
 */
class SampleAndCollect<H>(
    random: Random = Random(1),
    roundingStrategy: RoundingStrategy = RoundingStrategy.cumulative,
) : AmountBasedGroupSampler<ScalableVector, H>(
    random,
    { input ->
        val vectors = input.toList()
        val amounts = roundingStrategy.convertToInts(vectors.map { it.scalar })

        require(amounts.size == vectors.size) {
            "RoundingStrategy returned ${amounts.size} values for ${vectors.size} inputs"
        }

        vectors.zip(amounts).toMap()
    }
),
    ScalableVectorSampler<H>