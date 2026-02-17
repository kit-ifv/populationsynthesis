package population


interface Household<T> {
    val members: List<Person<T>>
    val size get() = members.size
}


class HouseholdImpl<T>(override val members: MutableList<Person<T>>) : Household<T>
