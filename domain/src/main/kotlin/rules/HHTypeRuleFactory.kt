package rules

import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.toRuleSet
import population.TypedHousehold
import population.householdtype.HouseholdType
import rules.measurements.HouseholdTypeDefinition

abstract class HHTypeRuleFactory<T, RS : RuleSet<TypedHousehold<*>>>(
    val targetExtractor: (T, HouseholdType) -> Number?,

    ) {


    fun buildRuleSet(input: T): RS {
        val ruleSet = HouseholdType.entries.mapNotNull { optionalRule(it, input)}.toRuleSet()
        return finalize(ruleSet, input)
    }

    protected abstract fun finalize(ruleSet: RuleSet<TypedHousehold<*>>, input: T): RS

    private fun optionalRule(householdType: HouseholdType, input: T): Rule<TypedHousehold<*>>? = getDefinition(householdType).makeOptionalRule(
        targetExtractor(input, householdType)
    )

    private fun getDefinition(target: HouseholdType): HouseholdTypeDefinition {
        return HouseholdTypeDefinition(target)
    }
}

class DefaultHHTypeRuleFactory<T>(
    targetExtractor: (T, HouseholdType) -> Number?

): HHTypeRuleFactory<T, RuleSet<TypedHousehold<*>>>(targetExtractor) {
    override fun finalize(
        ruleSet: RuleSet<TypedHousehold<*>>,
        input: T
    ): RuleSet<TypedHousehold<*>> {
        return ruleSet
    }
}