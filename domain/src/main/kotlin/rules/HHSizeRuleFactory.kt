package rules

import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.covered.ExplicitTargetCoverageGroup
import edu.kit.ifv.populationsynthesis.rules.toRuleSet
import population.Household
import rules.measurements.HouseholdSizeDefinition

abstract class HHSizeRuleFactory<T, RS : RuleSet<Household<*>>>(
    val equalTargetExtractor: (T, Int) -> Number?,
    val greaterEqualTargetExtractor: (T, Int) -> Number?,
) {


    fun buildFor5(input: T): RS {
        return buildRuleSet(4, input)
    }

    fun buildFor6(input: T): RS {
        return buildRuleSet(5, input)
    }

    fun buildRuleSet(lastExplicitSize: Int, input: T): RS {
        val equalityRules = (1..lastExplicitSize).map { optionalEqualityRule(it, input) }
        val greaterEqualsRule = optionalGreaterEqualsRule(lastExplicitSize + 1, input)

        return finalize((equalityRules + greaterEqualsRule).filterNotNull().toRuleSet(), input)

    }

    protected abstract fun finalize(ruleSet: RuleSet<Household<*>>, input: T): RS

    private fun optionalGreaterEqualsRule(target: Int, input: T): Rule<Household<*>>? =
        getGreaterEqualDefinition(target).makeOptionalRule(greaterEqualTargetExtractor(input, target))

    private fun optionalEqualityRule(
        i: Int,
        input: T
    ): Rule<Household<*>>? = getEqualDefinition(i).makeOptionalRule(equalTargetExtractor(input, i))

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

class DefaultHHSizeRuleFactory<T>(
    equalTargetExtractor: (T, Int) -> Number?,
    greaterEqualTargetExtractor: (T, Int) -> Number?
) : HHSizeRuleFactory<T, RuleSet<Household<*>>>(
    equalTargetExtractor, greaterEqualTargetExtractor
) {
    override fun finalize(
        ruleSet: RuleSet<Household<*>>,
        input: T
    ): RuleSet<Household<*>> {
        return ruleSet
    }
}

class CoveredHHSizeRuleFactory<T>(
    equalTargetExtractor: (T, Int) -> Number?,
    greaterEqualTargetExtractor: (T, Int) -> Number?,
    val totalTargetDeterminer: (T) -> Number,
) : HHSizeRuleFactory<T, ExplicitTargetCoverageGroup<Household<*>>>(equalTargetExtractor, greaterEqualTargetExtractor) {

    override fun finalize(
        ruleSet: RuleSet<Household<*>>,
        input: T
    ): ExplicitTargetCoverageGroup<Household<*>> {
        return ExplicitTargetCoverageGroup(ruleSet, totalTargetDeterminer(input))
    }
}