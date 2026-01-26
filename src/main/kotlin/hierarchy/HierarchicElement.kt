package edu.kit.ifv.populationsynthesis.hierarchy

interface HierarchicElement<T> {
    fun getParents(element: T): Collection<T>
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
