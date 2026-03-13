package population


interface Household<out T> {
    val members: List<Person<T>>
    val size get() = members.size
}


class HouseholdImpl<T>(override val members: MutableList<Person<T>>) : Household<T>
