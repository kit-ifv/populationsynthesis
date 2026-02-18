package edu.kit.ifv.populationsynthesis

import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector

/**
 * A signature represents a household in regard to a set of rules. An entry in the signature (k, v) represents that this
 * particular signature will return the value v for the k-th rule.
 *
 * Example the 3rd Rule is [amount of people with driver licence] and sig = {3:2.0} that would mean that two members of
 * the household have a driver licence.
 *
 * Note that the primitive signature definition is using boxing. This does not cause issues if the amount of rules
 * is below 128 due to Java Language Specification (JLS), §5.1.7 — Boxing Conversion
 * https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html
 *
 * Effectively meaning that if the number of rules is lower than 128 that the standard definition of map is sufficient.
 *
 * If this limit is exceeded maybe some tricks should be employed to maintain performance.
 *  -Djava.lang.Integer.IntegerCache.high=10000
 *  Or a manual cache of Values...
 */
typealias SignatureOld = Map<Int, Double>
//typealias Signature = Int2DoubleOpenHashMap
fun SignatureOld.toScalableVector(): ScalableVector = TODO()

class Signature(
    val indices: IntArray,
    val values: DoubleArray,
) {


    fun hasKey(index: Int): Boolean {
        return index in indices
    }

    operator fun get(index: Int): Double {
        val idx = indices
        val vals = values
        for (i in idx.indices) if (idx[i] == index) return vals[i]
        return 0.0
    }


    fun isRelevantFor(indices: Set<Int>): Boolean {
        return this.indices.any{it in indices}
    }


    fun filterKeys(predicate: (Int) -> Boolean): Signature{
        val relevantIndices = indices.filter(predicate)

        return Signature(relevantIndices.toIntArray(), relevantIndices.map { values[it] }.toDoubleArray())
    }

    override fun equals(other: Any?): Boolean {
        if(other !is Signature) return false
        return indices.contentEquals(other.indices) && values.contentEquals(other.values)

    }

    override fun hashCode(): Int {
        return indices.contentHashCode() + 31 * values.contentHashCode()
    }
    fun filter(predicate: (Pair<Int, Double>) -> Boolean): List<Pair<Int, Double>> = entries.filter(predicate)

    constructor(map: Map<Int, Double>) : this(
        map.keys.toIntArray(), map.values.toDoubleArray(),
    )
    val keys get() = indices
    val entries get() = indices.zip(values.toList())
    val maxKey get() = indices.last()
    override fun toString(): String {
        return entries.joinToString(", ", "{", "}")
    }
    fun isEmpty(): Boolean = indices.isEmpty()
    companion object {
        fun fromValues(values: Collection<Double>): Signature {
            val targetValues  = values.withIndex().filter { it.value != 0.0 }

            return Signature(targetValues.map { it.index }.toIntArray(), targetValues.map { it.value }.toDoubleArray())
        }
    }
}