package edu.kit.ifv.populationsynthesis.hierarchy

open class GraphHierarchy<T>(protected val hierarchyGraph: MutableHierarchyGraph<T>) :
    MutableHierarchicElement<T> by hierarchyGraph
