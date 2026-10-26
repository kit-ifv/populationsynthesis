package edu.kit.ifv.populationsynthesis.datastructures

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class SignatureTest {
    @Test
    fun testSignature() {
        val sigA = Signature.fromValues(1.0, 2.0, 3.0)

        val dot = sigA.dot(sigA)
        assertEquals(14.0, dot)
    }
}