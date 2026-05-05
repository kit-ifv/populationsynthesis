package edu.kit.ifv.populationsynthesis.domain.area

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.hierarchy.HierarchyGraphFactory

object HierarchyFactory {
    fun marneExample(): HierarchicElement<ARSKey> {
        /*
        I would highly discourage writing each relationship manually and suggest using a programmatic approach, but
        for the example it may help having a visual guide what exactly is happening right now
         */
        return HierarchyGraphFactory.asForest {

            addRelationship(ARSKey.DIEKHUSEN_FAHRSTEDT, ARSKey.MARNE_NORDSEE)
            addRelationship(ARSKey.FRIEDRICHSKOOG, ARSKey.MARNE_NORDSEE)
            addRelationship(ARSKey.HELSE, ARSKey.MARNE_NORDSEE)
            addRelationship(ARSKey.KAISER_WILHELM_KOOG, ARSKey.MARNE_NORDSEE)
            addRelationship(ARSKey.KRONPRINZENKOOG, ARSKey.MARNE_NORDSEE)
            addRelationship(ARSKey.MARNE_STADT, ARSKey.MARNE_NORDSEE)
            addRelationship(ARSKey.MARNERDEICH, ARSKey.MARNE_NORDSEE)
            addRelationship(ARSKey.NEUFELD, ARSKey.MARNE_NORDSEE)
            addRelationship(ARSKey.NEUFELDERKOOG, ARSKey.MARNE_NORDSEE)
            addRelationship(ARSKey.RAMHUSEN, ARSKey.MARNE_NORDSEE)
            addRelationship(ARSKey.SCHMEDESWURTH, ARSKey.MARNE_NORDSEE)
            addRelationship(ARSKey.TRENNEWURTH, ARSKey.MARNE_NORDSEE)
            addRelationship(ARSKey.VOLSEMENHUSEN, ARSKey.MARNE_NORDSEE)
        }
    }

    fun marneOnly(): HierarchicElement<ARSKey> {
        return HierarchyGraphFactory.asForest {
            addVertex(ARSKey.MARNE_NORDSEE)
        }
    }

    fun fromARSKeyset(keys: Collection<ARSKey>): HierarchicElement<ARSKey> {
        val leveledKeys = keys.groupBy { it.level }

        val descendingOrder = leveledKeys.entries.sortedByDescending { it.key.digits }

        return HierarchyGraphFactory.asForest {
            descendingOrder.zipWithNext().forEach {(lowerLevel, _) ->
                val betters = descendingOrder.dropWhile { it.key.digits >= lowerLevel.key.digits }.flatMap { it.value }
                lowerLevel.value.forEach {
                    val target = betters.firstOrNull {u -> it in u} ?: throw NoSuchElementException("ArsKey $it is lonely, there is no parent")
                    addRelationship(it, target)
                }
            }
        }



    }


}