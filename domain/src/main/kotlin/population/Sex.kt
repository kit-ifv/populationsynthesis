package population

import kotlin.random.Random

@JvmInline
value class Sex private constructor(val code: Int) {
    companion object {
        val MALE = Sex(1)
        val FEMALE = Sex(2)
        val UNKNOWN = Sex(9)
        val values = listOf(MALE, FEMALE)

        fun random(random: Random) = values.random(random)


    }
}