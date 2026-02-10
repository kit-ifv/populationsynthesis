package edu.kit.ifv.populationsynthesis.datasource

import edu.kit.ifv.populationsynthesis.datasource.input.ARSKey
import edu.kit.ifv.populationsynthesis.datasource.input.AreaHierarchy
import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.hierarchy.HierarchyGraphFactory
import edu.kit.ifv.populationsynthesis.rules.provider.MapRuleProvider
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider


object RuleProviderFactory {
    private val demographyInfo: CensusDemographyRuleCollector = CensusDemographyRuleCollector.fromResource()
    private val householdInfo: CensusHouseholdRuleCollector = CensusHouseholdRuleCollector.fromResource()
    fun marneExample(): RuleProvider<ARSKey, CensusHousehold> {
        return createRuleProvider {
            // load age info, but only for municipalities that are in MARNE_NORDSEE
            loadFromOtherRuleProvider(demographyInfo.ageProvider) {
                it.key in ARSKey.MARNE_NORDSEE && it.key.level == AreaHierarchy.GEMEINDE
            }
            // load household info, but only for MARNE_NORDSEE
            loadFromOtherRuleProvider(householdInfo.sizeProvider) {
                it.key == ARSKey.MARNE_NORDSEE
            }

        }
    }
}
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
}
fun createRuleProvider(lambda: MapRuleProvider<ARSKey, CensusHousehold>.() -> Unit): RuleProvider<ARSKey, CensusHousehold> {
    val ruleProvider = MapRuleProvider<ARSKey, CensusHousehold>()
    ruleProvider.lambda()
    return ruleProvider
}

