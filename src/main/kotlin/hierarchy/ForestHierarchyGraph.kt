package edu.kit.ifv.populationsynthesis.hierarchy

import org.jgrapht.Graphs

class ForestHierarchyGraph<T>: MutableHierarchyGraph<T>() {
    override fun addRelationship(child: T, parent: T) {
        if (parentGraph.containsVertex(child) && parentGraph.outgoingEdgesOf(child).isNotEmpty()) throw IllegalArgumentException("Child node $child already has a parent node, cannot overwrite to newParent=$parent")
        if (childGraph.containsVertex(child) && childGraph.containsVertex(parent) && reachable(childGraph, child, parent)) {
            throw IllegalArgumentException("Introducing $child->$parent would introduce a cycle in the hierarchy graph.")// or throw IllegalArgumentException("Adding $parent -> $child would create a cycle")
        }

        Graphs.addEdgeWithVertices(parentGraph, child, parent)
        Graphs.addEdgeWithVertices(childGraph, parent, child)
    }
}