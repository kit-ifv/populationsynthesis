package edu.kit.ifv.populationsynthesis.hierarchy

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

abstract class MutableHierarchyGraph<T> private constructor(
    override val parentGraph: DefaultDirectedGraph<T, DefaultEdge>,
    override val childGraph: DefaultDirectedGraph<T, DefaultEdge>,
) : HierarchyGraph<T>(parentGraph, childGraph), MutableHierarchicElement<T> {
    constructor() : this(DefaultDirectedGraph(DefaultEdge::class.java), DefaultDirectedGraph(DefaultEdge::class.java))


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

    protected fun <V, E> reachable(g: Graph<V, E>, start: V, goal: V): Boolean {
        if (start == goal) return true
        val seen = HashSet<V>()
        val q: ArrayDeque<V> = ArrayDeque()
        q.add(start)
        seen.add(start)

        while (q.isNotEmpty()) {
            val v = q.removeFirst()
            for (e in g.outgoingEdgesOf(v)) {
                val w = g.getEdgeTarget(e)
                if (w == goal) return true
                if (seen.add(w)) q.addLast(w)
            }
        }
        return false
    }
}

