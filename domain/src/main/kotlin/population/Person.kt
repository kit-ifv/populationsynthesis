package population

interface Person<T> {
    val attributes: T
}

inline val Person<out HasAge>.age get() = attributes.age
inline val Person<out HasSex>.sex get() = attributes.sex

class PersonImpl<T>(override val attributes: T) : Person<T>
