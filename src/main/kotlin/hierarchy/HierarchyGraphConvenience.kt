package edu.kit.ifv.populationsynthesis.hierarchy

/**
 * Convenience function to get vararg access.
 */
fun <T> HierarchyGraph<T>.groupByHighestAncestor(vararg elements: T): Map<T, Collection<T>> {
    return groupByHighestAncestor(elements.asList())
}