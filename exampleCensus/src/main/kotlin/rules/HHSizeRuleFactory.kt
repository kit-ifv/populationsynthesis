package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.input.rules.CensusHouseholdData
import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.covered.ExplicitTargetCoverageGroup
import edu.kit.ifv.populationsynthesis.rules.measurements.HouseholdSizeDefinition

object HHSizeRuleFactory {



    fun buildFor5(input: CensusHouseholdData): CoverageGroup<CensusHousehold> {
        return buildCoverageGroup(4, input)
    }

    fun buildFor6(input: CensusHouseholdData): CoverageGroup<CensusHousehold> {
        return buildCoverageGroup(5, input)
    }

    private fun buildCoverageGroup(lastExplicitSize: Int, input: CensusHouseholdData): CoverageGroup<CensusHousehold> {
        val equalityRules = (1..lastExplicitSize).map { optionalEqualityRule(it, input) }
        val greaterEqualsRule = optionalGreaterEqualsRule(lastExplicitSize + 1, input)

        val ruleSet = (equalityRules + greaterEqualsRule).filterNotNull().toRuleSet()
        return ExplicitTargetCoverageGroup(rules = ruleSet, target = requireNotNull(input.Insgesamt_val))
    }

    private fun optionalGreaterEqualsRule(target: Int, input: CensusHouseholdData): Rule<CensusHousehold>? =
        getGreaterEqualDefinition(target).makeOptionalRule(input.getHouseholdSizesGreaterEqualTo(target))

    private fun optionalEqualityRule(
        i: Int,
        input: CensusHouseholdData
    ): Rule<CensusHousehold>? = getEqualDefinition(i).makeOptionalRule(input.getHouseholdSizeTarget(i))

    /**
     * Theoretically it would be better to save the definitions instead of constantly reconstructing them, but
     * that is a performance issue that is going to be miniscule at best
     */
    private fun getEqualDefinition(size: Int): HouseholdSizeDefinition {
        return HouseholdSizeDefinition(size, HouseholdSizeDefinition.EqualityOp.EQUALS)
    }

    private fun getGreaterEqualDefinition(size: Int): HouseholdSizeDefinition {
        return HouseholdSizeDefinition(size, HouseholdSizeDefinition.EqualityOp.GREATER_OR_EQUAL)
    }

}