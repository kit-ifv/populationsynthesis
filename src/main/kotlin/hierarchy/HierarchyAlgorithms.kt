package edu.kit.ifv.populationsynthesis.hierarchy

/**
 * Performs a downward traversal starting at [node]. For each visited node:
 * - if [expansionPredicate] is true, the node is replaced by its immediate children. (Which can be none if a leaf is
 * expanded)
 * - otherwise, the node is kept in the result
 *
 * Returns the resulting frontier (nodes that were not expanded).
 */
// TODO this is only accurate on forests, augment it to support DAGs
fun <T> HierarchicElement<T>.expandIf(node: T, expansionPredicate: HierarchicElement<T>.(T) -> Boolean): Set<T> {
    val activeNodes: ArrayDeque<T> = ArrayDeque()
    activeNodes.add(node)
    val resultSet = mutableSetOf<T>()
    while (!activeNodes.isEmpty()) {
        val head = activeNodes.removeFirst()
        if (expansionPredicate(head)) {
            activeNodes.addAll(getImmediateChildren(head))
        } else {
            resultSet.add(head)
        }
    }
    return resultSet
}

// TODO this is only accurate on forests, augment it to support DAGs
fun <T> HierarchicElement<T>.levels(node: T): Sequence<Collection<T>> {

    return sequence {
        var current: Set<T> = setOf(node)
        while (current.isNotEmpty()) {
            val next = current.flatMap { getImmediateChildren(it) }.toSet()
            yield(current)
            current = next


        }
    }
}