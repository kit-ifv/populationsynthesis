package edu.kit.ifv.populationsynthesis.datastructures.linalg

import edu.kit.ifv.populationsynthesis.datastructures.Signature
import edu.kit.ifv.populationsynthesis.datastructures.toDoubleArray
//@Deprecated("Is Dense vector acschually")
//class ArrayScalableVector(private val array: DoubleArray, override var scalar: Double) :
//    ScalableVector(scalar, ) {
//    constructor(vararg numbers: Number) : this(numbers.toList())
//    constructor(numbers: Collection<Number>) : this(numbers.map { it.toDouble() }.toDoubleArray(), 1.0)
//    constructor(signature: Signature, scalar: Double) : this(signature.toDoubleArray(), scalar)
//    override val signature: Signature = Signature.Companion.fromValues(array.toList())
//
//
//    override fun currentValueForIndex(index: Int): Double {
//        return this.getAttribute(index) * scalar
//    }
//
//    override fun attributeForIndex(index: Int): Double {
//        return this.getAttribute(index)
//    }
//
//    private fun getAttribute(index: Int) = runCatching { array[index] }.getOrNull() ?: 0.0
//
//    val content get() = array.toList()
//    override fun appliesToRule(ruleIndex: Int): Boolean {
//        return this.getAttribute(ruleIndex) != 0.0
//    }
//
//    override fun equals(other: Any?): Boolean {
//        return if (other !is ArrayScalableVector) false
//        else array.contentEquals(other.array)
//    }
//
//    override fun hashCode(): Int {
//        return array.contentHashCode()
//    }
//
//}