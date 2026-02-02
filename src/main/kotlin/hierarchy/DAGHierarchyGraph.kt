package edu.kit.ifv.populationsynthesis.hierarchy

import org.jgrapht.Graphs

class DAGHierarchyGraph<T>(private val guard: T) : MutableHierarchyGraph<T>() {
    override fun addRelationship(child: T, parent: T) {
        if (childGraph.containsVertex(child) && childGraph.containsVertex(parent) && reachable(
                childGraph,
                child,
                parent
            )
        ) {
            throw IllegalArgumentException("Introducing $child->$parent would introduce a cycle in the hierarchy graph.")// or throw IllegalArgumentException("Adding $parent -> $child would create a cycle")
        }

        Graphs.addEdgeWithVertices(parentGraph, child, parent)
        Graphs.addEdgeWithVertices(childGraph, parent, child)
    }

    override fun groupByHighestAncestor(elements: Collection<T>): Map<T, Collection<T>> {
        // This grouping can be broken in the context of a DAG, because there could be multiple "highest parents"
        val targets = super.groupByHighestAncestor(elements)

        val owners: MutableMap<T, MutableSet<T>> = mutableMapOf()

        for ((k, values) in targets) {
            for (v in values) {
                owners.getOrPut(v) { mutableSetOf() }.add(k)
            }
        }
        // If a node is covered by multiple parents then all those parents must be collected under the guard node as a catch all mechanism
        val badParentNodes = owners.values.filter { it.size > 1 }.flatten().toSet()

        val newTargets = targets.toMutableMap()
        val allDuplicateNodes = badParentNodes.mapNotNull { targets[it] }.flatten().toSet()
        if (allDuplicateNodes.isNotEmpty()) {
            newTargets[guard] = allDuplicateNodes
            badParentNodes.forEach {
                newTargets.remove(it)
            }
        }

        return newTargets
    }
}