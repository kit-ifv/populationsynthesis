package edu.kit.ifv.populationsynthesis.rules.measurement

/**
 *  A measurement function converts an input element [T] to a numeric representation of whatever the underlying
 *  measurement will be. For example the Household H = { Person(age=30, sex=Male), Person(age=31, sex=Female)} could
 *  be measured like so: c = Measurement<Household> { it.members.count{ it.sex == Sex.Male) }
 *  c(H) = 1.0 would be this representation, as there is one male living in the household. However Household is just
 *  a concrete example instantiation. The type T is arbitrary, it could be households (and to be honest it always
 *  will be) but it could be anything:  workplaces, exotic fruits, or phylogenetic variants of rattlesnakes.
 *
 *  The core idea remains the same. A measurement function assigns a numeric "contribution" to the element, whatever
 *  its type may be.
 */
fun interface Measurement<in T> {
    fun measure(element: T): Double
}


