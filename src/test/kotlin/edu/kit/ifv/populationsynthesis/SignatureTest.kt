package edu.kit.ifv.populationsynthesis

import edu.kit.ifv.populationsynthesis.datastructures.Signature
import edu.kit.ifv.populationsynthesis.datastructures.linalg.hasKey
import edu.kit.ifv.populationsynthesis.datastructures.linalg.isRelevantForIndices
import kotlin.test.*

class SignatureTest {

    private val signature = Signature.fromValues(1, 0, 2, 0, 3)

    @Test
    fun testFiltering() {
        val mask = booleanArrayOf(false, false, true, false, false)
        val filteredSignature = signature.mask(mask)
        assertContentEquals(filteredSignature.indexArray.toList(), listOf(2))
        assertContentEquals(filteredSignature.valueArray.toList(), listOf(2.0))
    }

    @Test
    fun testRelevancy() {
        assertTrue(signature.isRelevantForIndices(setOf(0)))
        assertFalse(signature.isRelevantForIndices(setOf(1)))
    }

    @Test
    fun testHasKey() {
        assertTrue(signature.hasKey(0))
        assertFalse(signature.hasKey(42))
    }

    @Test
    fun testAccess() {
        assertEquals(3.0, signature[4])
        assertEquals(0.0, signature[3])
    }

    @Test
    fun testEquality() {
        val sig1 = Signature.fromValues(1.0, 2.0, 0.0, 3.0)
        val sig2 = Signature.fromMap(
            mapOf(
                0 to 1.0,
                1 to 2.0,
                2 to 0.0,
                3 to 3.0,
            )
        )

        assertEquals(sig1, sig2)
        assertEquals(sig1.hashCode(), sig2.hashCode())


    }
}