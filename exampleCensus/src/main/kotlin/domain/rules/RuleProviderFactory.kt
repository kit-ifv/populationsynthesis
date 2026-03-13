package edu.kit.ifv.populationsynthesis.domain.rules

import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import edu.kit.ifv.populationsynthesis.domain.area.AreaLevel
import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.rules.MutableRuleSet
import edu.kit.ifv.populationsynthesis.rules.provider.MapRuleProvider
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider

object RuleProviderFactory {
    private val demographyInfo: CensusDemographyRules = CensusDemographyRules.fromResource()
    private val householdInfo: CensusHouseholdRules = CensusHouseholdRules.fromResource()

    val keys = demographyInfo.ageProvider.getAllRules().keys

    fun marneExample(): RuleProvider<ARSKey, CensusHousehold> {
        return createRuleProvider {
            // load age info, but only for municipalities that are in MARNE_NORDSEE
            loadFromOtherRuleProvider(demographyInfo.ageProvider) {
                it.key in ARSKey.MARNE_NORDSEE && it.key.level == AreaLevel.GEMEINDE
            }
            // load sex info, but only for municipalities that are in MARNE_NORDSEE
            loadFromOtherRuleProvider(demographyInfo.sexProvider) {
                it.key in ARSKey.MARNE_NORDSEE && it.key.level == AreaLevel.GEMEINDE
            }
            // load household info, but only for MARNE_NORDSEE
            loadFromOtherRuleProvider(householdInfo.sizeProvider) {
                it.key == ARSKey.MARNE_NORDSEE
            }



        }
    }

    fun marneOnly() : RuleProvider<ARSKey, CensusHousehold> {
        return createRuleProvider {
            loadFromOtherRuleProvider(demographyInfo.ageProvider) {
                it.key == ARSKey.MARNE_NORDSEE
            }
            loadFromOtherRuleProvider(householdInfo.sizeProvider) {
                it.key == ARSKey.MARNE_NORDSEE
            }
        }
    }

    fun bavaria(): RuleProvider<ARSKey, CensusHousehold> {
        return createRuleProvider {
            loadFromOtherRuleProvider(demographyInfo.ageProvider) {
                it.key in ARSKey.OBERBAYERN && it.key.level == AreaLevel.GEMEINDE
            }
            // load sex info, but only for municipalities that are in MARNE_NORDSEE
            loadFromOtherRuleProvider(demographyInfo.sexProvider) {
                it.key in ARSKey.OBERBAYERN && it.key.level == AreaLevel.GEMEINDE
            }
            // load household info, but only for MARNE_NORDSEE
            loadFromOtherRuleProvider(householdInfo.sizeProvider) {
                it.key == ARSKey.OBERBAYERN
            }
        }
    }

    fun createRuleProvider(constructor: () -> MutableRuleSet<CensusHousehold> = ::MutableRuleSet, lambda: MapRuleProvider<ARSKey, CensusHousehold>.() -> Unit): RuleProvider<ARSKey, CensusHousehold> {
        val ruleProvider = MapRuleProvider<ARSKey, CensusHousehold>(construction = constructor)
        ruleProvider.lambda()
        return ruleProvider
    }
}