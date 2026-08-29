package edu.kit.ifv.populationsynthesis.algorithms

import edu.kit.ifv.populationsynthesis.Signature
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class SignatureScalableVectorTest {

    @Test

    fun properContent() {
        val v = SignatureScalableVector(Signature.fromMap(mapOf(0 to 1.0, 1 to 1.0)), 1.0)
        assertEquals(v.content(), listOf(1.0, 1.0))
    }

}