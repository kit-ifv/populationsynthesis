package edu.kit.ifv.populationsynthesis.hierarchy

class DAGHierarchyGraphBuilder<T>(private val guardNode: T) {
    private val graph = DAGHierarchyGraph(guardNode)

    fun build(lambda: DAGHierarchyGraph<T>.() -> Unit): DAGHierarchyGraph<T> {
        graph.apply(lambda)
        val allVertices = graph.getAllVertices()
        require(guardNode !in allVertices) {
            "Cannot introduce guard node, if it is already in use."
        }
        return graph

    }
}