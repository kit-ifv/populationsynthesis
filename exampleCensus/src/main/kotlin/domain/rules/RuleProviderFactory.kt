package edu.kit.ifv.populationsynthesis.domain.rules

import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import edu.kit.ifv.populationsynthesis.domain.area.AreaLevel
import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.rules.MutableRuleSet
import edu.kit.ifv.populationsynthesis.rules.provider.MapRuleProvider
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider
import java.util.BitSet

object RuleProviderFactory {
    private val demographyInfo: CensusDemographyRules = CensusDemographyRules.fromResource()
    private val householdInfo: CensusHouseholdRules = CensusHouseholdRules.fromResource()
    private val schoolInfo: CensusSchoolLevelRules = CensusSchoolLevelRules.fromResource()
    private val employmentStatusInfo: CensusEmploymentRules = CensusEmploymentRules.fromResource()
    private val schoolDegreeInfo: CensusHighestSchoolDegreeRules = CensusHighestSchoolDegreeRules.fromResource()
    val keys = demographyInfo.ageProvider.getAllRules().keys

    fun loadFlatProviderWithNumber(arsKey: ARSKey, number: Int): RuleProvider<ARSKey, CensusHousehold>  {

        val binaryString = Integer.toBinaryString(number)
        return createRuleProvider {
            if(binaryString.getOrNull(0) == '1') {
                loadFromOtherRuleProvider(demographyInfo.ageProvider) {
                    it.key == arsKey
                }
            }
            if(binaryString.getOrNull(1) == '1') {
                loadFromOtherRuleProvider(demographyInfo.sexProvider) {
                    it.key == arsKey
                }
            }
            if(binaryString.getOrNull(2) == '1') {
                loadFromOtherRuleProvider(householdInfo.sizeProvider) {
                    it.key == arsKey
                }
            }
            if(binaryString.getOrNull(3) == '1') {
                loadFromOtherRuleProvider(householdInfo.seniorenTypProvider) {
                    it.key == arsKey
                }
            }
            if(binaryString.getOrNull(4) == '1') {
                loadFromOtherRuleProvider(schoolInfo.genderedSchoolProvider) {
                    it.key == arsKey
                }
            }
            if(binaryString.getOrNull(5) == '1') {
                loadFromOtherRuleProvider(employmentStatusInfo.genderedEmploymentProvider) {
                    it.key == arsKey
                }
            }
            if(binaryString.getOrNull(6) == '1') {
                loadFromOtherRuleProvider(schoolDegreeInfo.genderedSchoolDegreeProvider) {
                    it.key == arsKey
                }
            }
        }
    }

    fun loadFlatProviderMax(arsKey: ARSKey) : RuleProvider<ARSKey, CensusHousehold> {
        return createRuleProvider {
            loadFromOtherRuleProvider(demographyInfo.ageProvider) {
                it.key == arsKey
            }
            loadFromOtherRuleProvider(demographyInfo.sexProvider) {
                it.key == arsKey
            }
            loadFromOtherRuleProvider(householdInfo.sizeProvider) {
                it.key == arsKey
            }
            loadFromOtherRuleProvider(householdInfo.seniorenTypProvider) {
                it.key == arsKey
            }
            loadFromOtherRuleProvider(schoolInfo.genderedSchoolProvider) {
                it.key == arsKey
            }
            loadFromOtherRuleProvider(employmentStatusInfo.genderedEmploymentProvider) {
                it.key == arsKey
            }
            loadFromOtherRuleProvider(schoolDegreeInfo.genderedSchoolDegreeProvider) {
                it.key == arsKey
            }

        }
    }

    fun loadFlatProviderMin(arsKey: ARSKey) : RuleProvider<ARSKey, CensusHousehold> {
        return createRuleProvider {
            loadFromOtherRuleProvider(demographyInfo.ageProvider) {
                it.key == arsKey
            }
            loadFromOtherRuleProvider(demographyInfo.sexProvider) {
                it.key == arsKey
            }
            loadFromOtherRuleProvider(householdInfo.sizeProvider) {
                it.key == arsKey
            }


        }
    }
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
            // load sex info, but only for municipalities that are in OBERBAYERN
            loadFromOtherRuleProvider(demographyInfo.sexProvider) {
                it.key in ARSKey.OBERBAYERN && it.key.level == AreaLevel.GEMEINDE
            }
            // load household info, but only for MARNE_NORDSEE
            loadFromOtherRuleProvider(householdInfo.sizeProvider) {
                it.key == ARSKey.OBERBAYERN
            }
        }
    }

    fun germany(): RuleProvider<ARSKey, CensusHousehold> {
        return createRuleProvider {
            loadFromOtherRuleProvider(demographyInfo.ageProvider) {
                 it.key.level == AreaLevel.GEMEINDE
            }
            // load sex info, but only for municipalities that are in MARNE_NORDSEE
            loadFromOtherRuleProvider(demographyInfo.sexProvider) {
                 it.key.level == AreaLevel.GEMEINDE
            }
            // load household info, but only for MARNE_NORDSEE
            loadFromOtherRuleProvider(householdInfo.sizeProvider) {
                it.key == ARSKey.GERMANY
            }
        }
    }



    fun createRuleProvider(constructor: () -> MutableRuleSet<CensusHousehold> = ::MutableRuleSet, lambda: MapRuleProvider<ARSKey, CensusHousehold>.() -> Unit): RuleProvider<ARSKey, CensusHousehold> {
        val ruleProvider = MapRuleProvider<ARSKey, CensusHousehold>(construction = constructor)
        ruleProvider.lambda()
        return ruleProvider
    }
}
