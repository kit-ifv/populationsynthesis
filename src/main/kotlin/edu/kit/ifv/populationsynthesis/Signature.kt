package edu.kit.ifv.populationsynthesis



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
 *
 *
 *  As it turns out, the map will still create boxed objects, which produces significant overhead. Using an array
 *  representation is significantly stronger. There is the option to use a Int2DoubleOpenHashMap, which uses arrays
 *  as underlying type, but since the signature will never be changed after construction, the added overhead of
 *  maintaining map
 */
class Signature internal constructor(
    private val indexArray: IntArray,
    val valueArray: DoubleArray,
) {

    private constructor(map: Map<Int, Double>) : this(
        map.keys.toIntArray(), map.values.toDoubleArray(),
    )


    val keys get() = indexArray.toList()
    val values get() = valueArray.toList()
    val entries: List<Pair<Int, Double>> get() = indexArray.zip(valueArray.toList())
    val maxKey get() = indexArray.last()



    fun hasKey(index: Int): Boolean {
        return index in indexArray
    }

    operator fun get(index: Int): Double {
        for (i in indexArray.indices) if (indexArray[i] == index) return valueArray[i]
        return 0.0
    }


    fun isRelevantFor(indices: Set<Int>): Boolean {
        return indexArray.any { it in indices }
    }


    fun filterKeys(predicate: (Int) -> Boolean): Signature {


        val relevantIndices = indexArray.withIndex().filter {
            predicate(it.value)
        }
        return Signature(
            relevantIndices.map { it.value }.toIntArray(),
            relevantIndices.map { valueArray[it.index] }.toDoubleArray()
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Signature) return false
        return indexArray.contentEquals(other.indexArray) && valueArray.contentEquals(other.valueArray)

    }

    override fun hashCode(): Int {
        return indexArray.contentHashCode() + 31 * valueArray.contentHashCode()
    }

    fun filter(predicate: (Pair<Int, Double>) -> Boolean): List<Pair<Int, Double>> = entries.filter(predicate)




    override fun toString(): String {
        return entries.joinToString(", ", "{", "}")
    }

    fun isEmpty(): Boolean = indexArray.isEmpty()

    companion object {
        fun fromMap(map: Map<Int, Double>): Signature {
            return Signature(map.filterValues { it != 0.0 })
        }

        fun fromValues(values: Collection<Double>): Signature {
            val targetValues = values.withIndex().filter { it.value != 0.0 }

            return Signature(targetValues.map { it.index }.toIntArray(), targetValues.map { it.value }.toDoubleArray())
        }
        fun fromValues(vararg values: Double): Signature = fromValues(values.toList())
    }
}