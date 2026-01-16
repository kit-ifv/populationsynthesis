package examples.layerscenario

class KonduriHousehold(
    val identifier: Int,
    val regionHouseholdType: RegionHouseholdType,
    val householdType: HouseholdType,
    val members: List<KonduriPerson>,
) {
    constructor(identifier: Int, regionCode: Int, householdCode: Int, members: List<KonduriPerson>) : this(
        identifier, RegionHouseholdType.decode(regionCode), HouseholdType.decode(householdCode), members,
    )
    override fun toString() = "Kon.HH.($identifier)"
    companion object {
        operator fun get(index: Int): KonduriHousehold  = all[index - 1]
        private var counter = 0
        fun build(rCode: Int, hCode: Int, p1Code: Int, p2Code: Int, p3Code: Int): KonduriHousehold {
            val people = mutableListOf<KonduriPerson>()
            repeat(p1Code) {
                people.add(KonduriPerson(1))
            }
            repeat(p2Code) {
                people.add(KonduriPerson(2))
            }
            repeat(p3Code) {
                people.add(KonduriPerson(3))
            }
            return KonduriHousehold(++counter,rCode, hCode, people)
        }

        val first = build(3, 1, 1, 1, 1)
        val second = build(1, 1, 1, 0, 1)
        val third = build(2, 1, 2, 1, 0)
        val fourth = build(1, 2, 1, 0, 2)
        val fifth = build(2, 2, 0, 2, 1)
        val sixth = build(3, 2, 1, 1, 0)
        val seventh = build(2, 2, 2, 1, 2)
        val eighth = build(3, 2, 1, 2, 0)

        val all: List<KonduriHousehold> = listOf(first, second, third, fourth, fifth, sixth, seventh, eighth)
    }

}


class KonduriPerson(
    val personType: PersonType,
) {
    constructor(code: Int): this(PersonType.decode(code))
}

enum class PersonType(val code: Int) {
    ONE(1), TWO(2), THREE(3);
    companion object {

        fun decode(code: Int): PersonType {
            return PersonType.entries.first { it.code == code }
        }
    }
}

enum class HouseholdType(val code: Int) {
    ONE(1), TWO(2);
    companion object {

        fun decode(code: Int): HouseholdType {
            return entries.first { it.code == code }
        }
    }
}

enum class RegionHouseholdType(val code: Int) {
    ONE(1), TWO(2), THREE(3);

    companion object {

        fun decode(code: Int): RegionHouseholdType {
            return entries.first { it.code == code }
        }
    }
}