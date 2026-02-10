package examples.layerscenario

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchyGraph
import edu.kit.ifv.populationsynthesis.hierarchy.HierarchyGraphFactory

/**
 * This is the domain implementation of the example IPU from the Konduri paper: https://journals.sagepub.com/doi/pdf/10.3141/2563-08
 * Which is used in the tests.
 */
interface KonduriArea

object KonduriRegion : KonduriArea

class KonduriGeographicUnit private constructor(val code: Int) : KonduriArea {

    override fun toString(): String {
        return "Geo $code"
    }

    companion object {
        val geo1 = KonduriGeographicUnit(1)
        val geo2 = KonduriGeographicUnit(2)
    }
}

val KonduriGraph: HierarchyGraph<KonduriArea> = HierarchyGraphFactory.asForest {
    val top = KonduriRegion


    addVertex(top)

    addVertex(KonduriGeographicUnit.geo1)

    addVertex(KonduriGeographicUnit.geo2)
    addRelationship(KonduriGeographicUnit.geo1, top)
    addRelationship(KonduriGeographicUnit.geo2, top)
}