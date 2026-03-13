package edu.kit.ifv.populationsynthesis

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SignatureTest {

    private val signature = Signature(intArrayOf(0, 2, 4), doubleArrayOf(1.0, 2.0, 3.0))
    @Test
    fun testFiltering() {
        val mask = booleanArrayOf(false, false, true, false, false)
        val filteredSignature = signature.filterKeys { mask[it] }
        assertContentEquals(filteredSignature.keys, listOf(2))
        assertContentEquals(filteredSignature.values, listOf(2.0))
    }
    @Test
    fun testRelevancy() {
        assertTrue(signature.isRelevantFor(setOf(0)))
        assertFalse(signature.isRelevantFor(setOf(1)))
    }

    @Test
    fun testHasKey() {
        assertTrue(signature.hasKey(0))
        assertFalse(signature.hasKey(42))
    }
    @Test
    fun testAccess() {
        assertEquals(signature[4], 3.0)
        assertEquals(signature[3], 0.0)
    }

    @Test
    fun testEquality() {
        val sig1 = Signature.fromValues(1.0, 2.0,0.0, 3.0)
        val sig2 = Signature.fromMap(mapOf(
            0 to 1.0,
            1 to 2.0,
            2 to 0.0,
            3 to 3.0,
        ))

        assertEquals(sig1, sig2)
        assertEquals(sig1.hashCode(), sig2.hashCode())


    }
}