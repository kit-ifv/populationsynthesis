package edu.kit.ifv.populationsynthesis.hierarchy

object HierarchyGraphFactory {
    fun <T> asForest(lambda: ForestHierarchyGraph<T>.() -> Unit): HierarchyGraph<T> {
        return ForestHierarchyGraphBuilder<T>().build(lambda)
    }

    /**
     * For the construction of a DAG we require a [guard] node that is not part of the entire graph structure.
     * Because a DAG would otherwise not allow a reasonable grouping by parent.
     */
    fun <T> asDAG(guard: T, lambda: DAGHierarchyGraph<T>.() -> Unit): HierarchyGraph<T> {
        return DAGHierarchyGraphBuilder(guard).build(lambda)
    }
}