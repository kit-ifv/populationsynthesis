package population

interface Person<out T> {
    val attributes: T
}

inline val Person<HasAge>.age get() = attributes.age
inline val Person<HasBiologicalSex>.sex get() = attributes.sex

class PersonImpl<T>(override val attributes: T) : Person<T>
