package edu.kit.ifv.populationsynthesis.algorithms

import edu.kit.ifv.populationsynthesis.datastructures.Signature
import edu.kit.ifv.populationsynthesis.datastructures.linalg.NormalizedSignatureVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.SignatureScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.normalize
import edu.kit.ifv.populationsynthesis.datastructures.linalg.toSignatureVector
import org.junit.jupiter.api.Assertions.*
import kotlin.math.cos
import kotlin.test.Test

class NormalizedSignatureVectorTest {
    val difference = _root_ide_package_.edu.kit.ifv.populationsynthesis.datastructures.linalg.ConstantVector(
        doubleArrayOf(
            84.29130711284023,
            138.24318862508116,
            115.09969316983455,
            -3.858987595699091,
            -23.028716558175063,
            138.63531403552042,
            558.8458158307651,
            -490.0041449430337,
            -245.58524926105997,
            -201.92780128744198,
            3.753862733215101,
            6.986404606312135,
            63.63505202863507,
            951.6935036620698,
            -424.4342303667172,
            27.585210411949788,
            -60.1155419319648,
            99.9584479701536
        )
    )
    @Test
    fun checkCosineEquality() {

        val original = SignatureScalableVector(
            Signature(
                intArrayOf(6, 7, 8, 11, 12, 15),
                doubleArrayOf(1.0, 1.0, 1.0, 2.0, 1.0, 1.0)
            ),
            15.61930365149324
        )

        val newVector = NormalizedSignatureVector(
            Signature(
                intArrayOf(6, 7, 8, 11, 12, 15),
                doubleArrayOf(
                    0.3333333333333333,
                    0.3333333333333333,
                    0.3333333333333333,
                    0.6666666666666666,
                    0.3333333333333333,
                    0.3333333333333333
                )
            ),
            46.85791095447972
        )

        val a = original.safeCosineSimilarity(difference)
        // Dot = -1117.5690908797521
        // this length = 46.85791095447972
        // other length = 1347.9883315334278
        val b = newVector.safeCosineSimilarity(difference)
        assertEquals(a, b, 0.000001)
    }

    @Test
    fun simpleCosineEquality() {
        val original = SignatureScalableVector(
            Signature(
                intArrayOf(0, 1),
                doubleArrayOf(1.0, 1.0)
            ),
            1.0
        )
        val newVector = original.normalize()
        val target =
            _root_ide_package_.edu.kit.ifv.populationsynthesis.datastructures.linalg.ConstantVector(listOf(1.0, 0.0))
        assertEquals(cos(0.785398), original.safeCosineSimilarity(target), 0.00001)
        assertEquals(cos(0.785398), newVector.safeCosineSimilarity(target), 0.00001)
    }
    @Test
    fun cosineEqualityOther() {

        val original = SignatureScalableVector(
            Signature(
                intArrayOf(6, 11, 12, 17),
                doubleArrayOf(7.0, 3.0, 4.0, 1.0)
            ),
            0.09054668783474341
        )

        val newVector = NormalizedSignatureVector(
            Signature(
                intArrayOf(6, 11, 12, 17),
                doubleArrayOf(
                    0.808290376865476, 0.3464101615137754, 0.46188021535170054, 0.11547005383792514
                )
            ),
            0.784157318934272
        )

        val a = original.safeCosineSimilarity(difference)
        // Dot = -1117.5690908797521
        // this length = 46.85791095447972
        // other length = 1347.9883315334278
        val b = newVector.safeCosineSimilarity(difference)
        assertEquals(a, b, 0.000001)

    }

    @Test
    fun reasonableIterator() {
        val vector = NormalizedSignatureVector(Signature.fromValues(1.0), scalar = 4.0)
        assertEquals(vector.map { it }, listOf(4.0))
    }
    @Test
    fun properConvergence() {
        val vector = _root_ide_package_.edu.kit.ifv.populationsynthesis.datastructures.linalg.ConstantVector(
            doubleArrayOf(
                282.0,
                358.0,
                458.0,
                656.0,
                390.0,
                763.0,
                1974.0,
                3552.0,
                1448.0,
                1372.0,
                1589.0,
                6318.0,
                6514.0,
                2590.0,
                2329.0,
                753.0,
                547.0,
                294.0
            )
        )
        assertEquals(18, vector.size)
        val transformed = vector.toSignatureVector()
        assertEquals(18, transformed.size)
    }

}