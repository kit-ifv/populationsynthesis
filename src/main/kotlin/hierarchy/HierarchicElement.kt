package edu.kit.ifv.populationsynthesis.hierarchy

interface HierarchicElement<T> {
    fun getParent(element: T): T?
    fun getAllAncestors(element: T): Collection<T>
    fun getImmediateChildren(element: T): List<T>
    fun getAllVertices(): Collection<T>
    fun getAllLeafs(): List<T>

    fun partition(predicate: (T) -> Boolean): Pair<HierarchicElement<T>, HierarchicElement<T>>

    /**
     * Return all descendant nodes, except the element node.
     */
    fun getAllChildren(element: T): Collection<T>
    fun getAllLeafsFrom(element: T): Collection<T> = getAllChildren(element).filter { isLeaf(it) }

    fun isLeaf(element: T) = getImmediateChildren(element).isEmpty()

    /**
     * Given a set of nodes, construct the forest that spawns when using these elements as leaf nodes.
     * (Remember graph is a directed acyclic graph so this should work)
     */
    fun groupByHighestAncestor(elements: Collection<T> = getAllLeafs()): Map<T, Collection<T>>



    fun getDependencies(elements: Collection<T>): Map<T, List<T>> {
        return elements.associateWith {
            val descendants = getAllChildren(it).toSet()
            elements.filter { it in descendants }
        }.filterValues {
            it.isNotEmpty()
        }
    }
}
/**
 * Performs a downward traversal starting at [node]. For each visited node:
 * - if [expansionPredicate] is true, the node is replaced by its immediate children. (Which can be none if a leaf is
 * expanded)
 * - otherwise, the node is kept in the result
 *
 * Returns the resulting frontier (nodes that were not expanded).
 */
fun <T> HierarchicElement<T>.expandIf(node: T, expansionPredicate: (T) -> Boolean) :Set<T> {
    val activeNodes : ArrayDeque<T> = ArrayDeque()
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
