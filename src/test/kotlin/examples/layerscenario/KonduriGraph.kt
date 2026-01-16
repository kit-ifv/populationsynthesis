package examples.layerscenario

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchyGraph
import edu.kit.ifv.populationsynthesis.hierarchy.MutableHierarchyGraph

interface KonduriArea

object KonduriRegion: KonduriArea

class KonduriGeographicUnit private constructor(val code: Int): KonduriArea {

    override fun toString(): String {
        return "Geo $code"
    }
    companion object {
        val geo1 = KonduriGeographicUnit(1)
        val geo2 = KonduriGeographicUnit(2)
    }
}

val KonduriGraph: HierarchyGraph<KonduriArea> = MutableHierarchyGraph<KonduriArea>().apply {
    val top = KonduriRegion


    addVertex(top)

    addVertex(KonduriGeographicUnit.geo1)

    addVertex(KonduriGeographicUnit.geo2)
    addRelationship(KonduriGeographicUnit.geo1, top)
    addRelationship(KonduriGeographicUnit.geo2, top)
}