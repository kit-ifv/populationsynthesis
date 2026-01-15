package edu.kit.ifv.populationsynthesis.hierarchy

import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

class MutableHierarchyGraph<T> private constructor(
    override val parentGraph: DefaultDirectedGraph<T, DefaultEdge>,
    override val childGraph: DefaultDirectedGraph<T, DefaultEdge>,
) : HierarchyGraph<T>(parentGraph, childGraph), MutableHierarchicElement<T> {
    constructor() : this(DefaultDirectedGraph(DefaultEdge::class.java), DefaultDirectedGraph(DefaultEdge::class.java))

    override fun addRelationship(child: T, parent: T) {
        if (parentGraph.containsVertex(child) && parentGraph.outgoingEdgesOf(child).isNotEmpty()) return
        Graphs.addEdgeWithVertices(parentGraph, child, parent)
        Graphs.addEdgeWithVertices(childGraph, parent, child)
    }

    override fun removeVertex(target: T) {
        parentGraph.removeVertex(target)
        childGraph.removeVertex(target)
    }

    override fun removeVertices(targets: Collection<T>) {
        parentGraph.removeAllVertices(targets)
        childGraph.removeAllVertices(targets)
    }

    override fun addVertex(target: T) {
        parentGraph.addVertex(target)
        childGraph.addVertex(target)
    }
}