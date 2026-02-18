package edu.kit.ifv.populationsynthesis.algorithms

import edu.kit.ifv.populationsynthesis.SignatureOld
import edu.kit.ifv.populationsynthesis.Signature
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.measurement.Measurement

interface ScalableVector {
    var scalar: Double
    val signature: Signature
    @Deprecated("I would like to drop this field.")
    val size: Int
    fun currentValueForIndex(index: Int): Double
    fun attributeForIndex(index: Int): Double
    fun appliesToRule(ruleIndex: Int): Boolean
    @Deprecated("I would like to drop this method.")
    fun content(maxSize: Int): List<Double>

    companion object {
        /**
         * creates a Scalable Vector for a target [element] based on the ruleset defined in [rules]
         */
        fun <T> createFromRules(element: T, rules: Collection<Rule<T>>): ArrayScalableVector {
            return createFromLogics(element, rules.map { it.logic })
        }

        fun <T> createFromLogics(element: T, logics: Collection<Measurement<T>>): ArrayScalableVector {
            return ArrayScalableVector(logics.map { it.measure(element) })
        }

        fun <T> createFrom(element: T, measurements: Collection<Measurement<T>>): ArrayScalableVector {
            return ArrayScalableVector(measurements.map { it.measure(element) })
        }
    }
}

class SignatureScalableVector(override val signature: Signature, override var scalar: Double) : ScalableVector {
    override val size: Int
        get() = signature.maxKey

    override fun currentValueForIndex(index: Int): Double {
        return signature[index] * scalar
    }

    override fun attributeForIndex(index: Int): Double {
        return signature[index]
    }

    override fun appliesToRule(ruleIndex: Int): Boolean {
        return signature.hasKey(ruleIndex)
    }

    override fun content(maxSize: Int): List<Double> {
        return (0 until  signature.maxKey).map { signature[it] }
    }
}

class ArrayScalableVector internal constructor(private val array: DoubleArray, override var scalar: Double) : ScalableVector {
    internal constructor(vararg numbers: Number) : this(numbers.toList())
    internal constructor(numbers: Collection<Number>) : this(numbers.map { it.toDouble() }.toDoubleArray(), 1.0)
    override val signature: Signature = Signature.fromValues(array.toList())
    override val size: Int = array.size
    override fun currentValueForIndex(index: Int): Double {
        return array[index] * scalar
    }

    override fun attributeForIndex(index: Int): Double {
        return array[index]
    }

    override fun content(maxSize: Int): List<Double> {
        require(maxSize == size) {
            "This should not happen"
        }
        return array.toList()
    }
    val content get() = array.toList()
    override fun appliesToRule(ruleIndex: Int): Boolean {
        return array[ruleIndex] != 0.0
    }

    override fun equals(other: Any?): Boolean {
        return if(other !is ArrayScalableVector) false
        else array.contentEquals(other.array)
    }

    override fun hashCode(): Int {
        return array.contentHashCode()
    }
}



//class MapScalableVector(private val , override var scalar: Double = 1.0): ScalableElement {
//    override fun currentValueForIndex(index: Int): Double {
//        return signature[index]  * scalar
//    }
//
//    override fun attributeForIndex(index: Int): Double {
//        return signature[index]
//    }
//
//    override fun appliesToRule(ruleIndex: Int): Boolean {
//        return signature[ruleIndex] != 0.0
//    }
//}

/**
 * A [ScalableVectore] represents a vectorized encoding of household attributes, where each element of the vector
 * corresponds to a specific attribute, and the `scalar` factor indicates how many instances of the household encoding
 * are currently present. The vector is immutable, but the scalar factor is mutable, allowing for optimizations and adjustments
 * during processing.
 *
 * The vector itself is represented as an integer array, where each element typically represents an integer encoding of a
 * household's attribute (e.g., [0, 1, 2] could represent a household with the ruleset
 * ["Household Size == 1", "Household Size == 2", "Number of LicenceHolders"])
 *
 * This class provides a read-only view of the vector's content and exposes operations to manipulate its scalar,
 * evaluate its values, and check if it applies to a specific rule.
 *
 * @param vector The integer array representing the attribute values of the household encoding.
 * @param scalar The scaling factor applied to the vector, representing how many instances of the household are desired (default is 1.0).
 */
class ScalableVectore internal constructor(vector: Collection<Double>, var scalar: Double = 1.0) {

    internal constructor(vararg numbers: Number) : this(numbers.map { it.toDouble() }, 1.0)
    private val array: DoubleArray = vector.toDoubleArray()
    val signature: SignatureOld = array.withIndex().filter { it.value != 0.0 }.associate { (i, value) -> i to value }
    val size : Int get() = array.size
    /**
     * A read-only property that provides a list view of the [array] for external access.
     * The underlying array is unfortunately mutable, but the list provides an immutable view to prevent external modification.
     */
    val content: List<Double> = array.toList()

    /**
     * Returns the value of the vector at the specified [index], adjusted by the current [scalar].
     *
     * @param index The index in the vector for which the value should be retrieved.
     * @return The value at the specified [index] in the vector, multiplied by the [scalar].
     */
    fun currentValueForIndex(index: Int): Double = array[index] * scalar

    fun attributeForIndex(index: Int): Double = array[index]

    /**
     * Determines whether this vector applies to a given rule based on the value at the [ruleIndex].
     * A value other than 0 at the [ruleIndex] indicates that the vector applies to the rule.
     *
     * @param ruleIndex The index of the rule to check against in the vector.
     * @return True if the vector applies to the rule (i.e., the value at the [ruleIndex] is non-zero), otherwise false.
     */
    fun appliesToRule(ruleIndex: Int): Boolean {
        return array[ruleIndex] != 0.0
    }

    /**
     * Multiplies the [scalar] of the vector by the given [times] value.
     *
     * @param times The number by which to multiply the current scalar.
     */
    operator fun timesAssign(times: Number) {
        scalar *= times.toDouble()
    }

//    override fun equals(other: Any?): Boolean {
//        if (other !is ScalableVector) {
//            return false
//        }
//        return array.contentEquals(other.array)
//    }

    override fun hashCode(): Int {
        return array.contentHashCode()
    }

    override fun toString(): String {
        return "ScalableVector(scalar=$scalar) $content"
    }

//    companion object {
//        /**
//         * creates a Scalable Vector for a target [element] based on the ruleset defined in [rules]
//         */
//        fun <T> createFromRules(element: T, rules: Collection<Rule<T>>): ScalableVector {
//            return createFromLogics(element, rules.map { it.logic })
//        }
//
//        fun <T> createFromLogics(element: T, logics: Collection<Measurement<T>>): ScalableVector {
//            return ScalableVector(logics.map { it.measure(element) })
//        }
//
//        fun <T> createFrom(element: T, measurements: Collection<Measurement<T>>): ScalableVector {
//            return ScalableVector(measurements.map { it.measure(element) })
//        }
//    }
}
