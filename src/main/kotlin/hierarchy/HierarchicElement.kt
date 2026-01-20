package edu.kit.ifv.populationsynthesis.hierarchy

import org.jgrapht.traverse.BreadthFirstIterator
import java.util.PriorityQueue
import java.util.Queue

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

fun <T> HierarchicElement<T>.downwardBFS(node: T, lambda: (T) -> Boolean) :Set<T> {
    val activeNodes : ArrayDeque<T> = ArrayDeque()
    activeNodes.add(node)
    val resultSet = mutableSetOf<T>()
    while (!activeNodes.isEmpty()) {
        val head = activeNodes.removeFirst()
        if (lambda(head)) {
            println("Adding children ${getImmediateChildren(head)}")
            activeNodes.addAll(getImmediateChildren(head))
        } else {
            println("Adding head ${head}")
            resultSet.add(head)
        }
    }
    return resultSet
}

/**
 * Lambda Boolean when the node should be replaced with its parent node.
 */
fun <T> HierarchicElement<T>.upwardBFS(leafs: Collection<T>, lambda: (T) -> Boolean) :Set<T> {

    val activeNodes  = ArrayDeque(leafs)
    val handledNodes: MutableSet<T> = mutableSetOf()
    val resultSet = mutableSetOf<T>()
    while (!activeNodes.isEmpty()) {
        val head = activeNodes.removeFirst()
        handledNodes.add(head)
        if (lambda(head)) {
            getParent(head)?.let { parent ->
                if(parent !in handledNodes) {
                    activeNodes.add(parent)
                }
            }

        } else {
            resultSet.add(head)
        }
    }
    return resultSet
}

