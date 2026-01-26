package examples.threelayerscenario

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchyGraph
import edu.kit.ifv.populationsynthesis.hierarchy.HierarchyGraphFactory

internal val ABCGraph: HierarchyGraph<Area> = HierarchyGraphFactory.asForest {
    addRelationship(A.A1, B.B1)
    addRelationship(A.A2, B.B1)
    addRelationship(A.A3, B.B2)
    addRelationship(A.A4, B.B2)
    addRelationship(B.B1, C.C1)
    addRelationship(B.B2, C.C1)
    addRelationship(A.A5, B.B3)
    addRelationship(B.B3, C.C2)
}
