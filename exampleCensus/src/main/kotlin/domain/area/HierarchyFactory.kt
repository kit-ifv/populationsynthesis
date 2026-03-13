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

            addRelationship(ARSKey.Companion.DIEKHUSEN_FAHRSTEDT, ARSKey.Companion.MARNE_NORDSEE)
            addRelationship(ARSKey.Companion.FRIEDRICHSKOOG, ARSKey.Companion.MARNE_NORDSEE)
            addRelationship(ARSKey.Companion.HELSE, ARSKey.Companion.MARNE_NORDSEE)
            addRelationship(ARSKey.Companion.KAISER_WILHELM_KOOG, ARSKey.Companion.MARNE_NORDSEE)
            addRelationship(ARSKey.Companion.KRONPRINZENKOOG, ARSKey.Companion.MARNE_NORDSEE)
            addRelationship(ARSKey.Companion.MARNE_STADT, ARSKey.Companion.MARNE_NORDSEE)
            addRelationship(ARSKey.Companion.MARNERDEICH, ARSKey.Companion.MARNE_NORDSEE)
            addRelationship(ARSKey.Companion.NEUFELD, ARSKey.Companion.MARNE_NORDSEE)
            addRelationship(ARSKey.Companion.NEUFELDERKOOG, ARSKey.Companion.MARNE_NORDSEE)
            addRelationship(ARSKey.Companion.RAMHUSEN, ARSKey.Companion.MARNE_NORDSEE)
            addRelationship(ARSKey.Companion.SCHMEDESWURTH, ARSKey.Companion.MARNE_NORDSEE)
            addRelationship(ARSKey.Companion.TRENNEWURTH, ARSKey.Companion.MARNE_NORDSEE)
            addRelationship(ARSKey.Companion.VOLSEMENHUSEN, ARSKey.Companion.MARNE_NORDSEE)
        }
    }

    fun marneOnly(): HierarchicElement<ARSKey> {
        return HierarchyGraphFactory.asForest {
            addVertex(ARSKey.Companion.MARNE_NORDSEE)
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