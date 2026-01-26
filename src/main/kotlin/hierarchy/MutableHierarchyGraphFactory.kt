package edu.kit.ifv.populationsynthesis.hierarchy

object MutableHierarchyGraphFactory {

    fun <T> asForest(lambda: ForestHierarchyGraph<T>.() -> Unit): ForestHierarchyGraph<T> = ForestHierarchyGraphBuilder<T>().build(lambda)
    fun <T> asDAG(guard: T, lambda: DAGHierarchyGraph<T>.() -> Unit): DAGHierarchyGraph<T> {
        return DAGHierarchyGraphBuilder(guard).build(lambda)
    }
}