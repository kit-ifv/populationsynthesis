package edu.kit.ifv.populationsynthesis.domain.population

import com.fasterxml.jackson.annotation.JsonCreator
import kotlin.random.Random

@JvmInline
value class Sex private constructor(val code: Int) {
    companion object {
        val MALE = Sex(1)
        val FEMALE = Sex(2)
        val UNKNOWN = Sex(9)
        val values = listOf(MALE, FEMALE)

        fun random(random: Random) = values.random(random)


        @JvmStatic
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        fun fromCode(code: Int): Sex =
            when (code) {
                1 -> MALE
                2 -> FEMALE
                9 -> UNKNOWN
                else -> throw IllegalArgumentException("Unknown Sex code: $code")
            }
    }
}