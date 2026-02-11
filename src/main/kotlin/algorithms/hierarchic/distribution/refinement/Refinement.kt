package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.refinement

import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.Partition

fun interface Refinement {
    fun refine(partitions: List<Partition>)
}