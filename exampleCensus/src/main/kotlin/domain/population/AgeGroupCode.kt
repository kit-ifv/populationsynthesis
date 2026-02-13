package edu.kit.ifv.populationsynthesis.domain.population

/**
 * Codes should never be exposed as a raw number. We can do this in Kotlin with zero overhead
 * by declaring a value class. This class for example represents all the codes that can occur
 * in the census dataset. Also we can better capsule what these codes stand for, in this case
 * they represent specifically designed ranges of ages. [0-2], [3-5] etc.
 *
 */
@JvmInline
value class AgeGroupCode private constructor(val rawCode: Int) {

    val ageInterval get() = intervals[rawCode - 1]

    companion object {
        val ALL = (1..11).map { AgeGroupCode(it) }
        val intervals = listOf(
            0..2,
            3..5,
            6..9,
            10..15,
            16..18,
            19..24,
            25..39,
            40..59,
            60..66,
            67..74,
            75..Int.MAX_VALUE,
        )

        operator fun get(code: Int): AgeGroupCode {
            return ALL[code - 1]
        }
    }
}