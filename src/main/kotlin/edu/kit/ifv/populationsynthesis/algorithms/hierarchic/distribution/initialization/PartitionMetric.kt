package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.initialization

import edu.kit.ifv.populationsynthesis.Signature

fun interface PartitionMetric {
    fun calculate(expected: DoubleArray, actual: DoubleArray, signature: Signature): Double
}

@Suppress("MagicNumber")
val SquaredDiff = PartitionMetric { expected, actual, signature ->
    var origDiff = .0
    var newDiff = .0
    signature.forEachEntry { k, v ->
        val exp = expected[k]
        val act = actual[k]
        val denom = if (exp == 0.0) 1e-9 else exp // avoid div by 0
        val delta = v
        val relErrorOrig = (exp - act) / denom
        val relErrorNew = (exp - (act + delta)) / denom

        origDiff += relErrorOrig * relErrorOrig
        newDiff += relErrorNew * relErrorNew

    }
//    signature.entries.forEach { (k, v) ->
//        val exp = expected[k]
//        val act = actual[k]
//        val denom = if (exp == 0.0) 1e-9 else exp // avoid div by 0
//        val delta = v
//        val relErrorOrig = (exp - act) / denom
//        val relErrorNew = (exp - (act + delta)) / denom
//
//        origDiff += relErrorOrig * relErrorOrig
//        newDiff += relErrorNew * relErrorNew
//    }

    origDiff - newDiff
}
