package edu.kit.ifv.populationsynthesis.hierarchy

class ForestHierarchyGraphBuilder<T>() {
    private val graph = ForestHierarchyGraph<T>()
    fun build(lambda: ForestHierarchyGraph<T>.()->Unit): ForestHierarchyGraph<T> {
        return graph.apply(lambda)
    }
}