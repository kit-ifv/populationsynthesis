package edu.kit.ifv.populationsynthesis.hierarchy

interface MutableHierarchicElement<T> : HierarchicElement<T> {
    fun addRelationship(child: T, parent: T)
    fun addVertex(target: T)
    fun removeVertex(target: T)
    fun removeVertices(targets: Collection<T>)
}

interface MutableHierarchicElementWithLeafType<T> : MutableHierarchicElement<T> {

}