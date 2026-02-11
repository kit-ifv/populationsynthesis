package edu.kit.ifv.populationsynthesis.datasource

import kotlin.random.Random


class CensusHousehold (
    val members: List<CensusPerson>
) {
    constructor(vararg ages: Int) : this(ages.map { CensusPerson(it) })
    companion object {
        fun random(seed: Random = Random(1)): CensusHousehold {
            val size = (1..5).random(seed)
            return fixedSize(size, seed)
        }

        fun fixedSize(size: Int, seed: Random = Random(1)): CensusHousehold {
            val members = (0 until size).map {
                CensusPerson.random(seed)
            }
            return CensusHousehold(members)
        }
        fun randoms(amount: Int, seed: Random = Random(1)): List<CensusHousehold> {
            return (0 until amount).map { random(seed) }
        }


    }
}
data class CensusPerson(
    val age: Int,
) {
    companion object {
        fun random(seed: Random = Random(1)): CensusPerson {
            return CensusPerson(age = (0..100).random(seed))
        }
    }
}