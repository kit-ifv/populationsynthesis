package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.covered.FullCoverageGroup

/**
 * An exhaustive rule generator knows that the generated rules are covering all potential attributes that the elements
 * could possibly exert. So for example when the observed attribute is household size, the Rules (size == 1) (size == 2)
 * (size >=3) cover all potential representations (A household will always fall in one of these categories). In case
 * that an attribute space is completely covered additional statistical analysis can be done. For example we can
 * calculate relative shares like 15% of households are single sized from absolute values. We could not do that if we
 * would miss an attribute representation.
 *
 * The information whether an information space is covered cannot be determined (Theoretically the entire solution space
 * could be iterated to check whether a rule collection is covering, but that is a lot of effort)
 *
 * Instead you, the developer, should use this interface to indicate coverage. If you know that an attribute will be
 * covered by the generated rules.
 *
 * Coverage group is essentially a list with added benefits.
 */
interface ExhaustiveRuleGenerator<T> : RuleGenerator<T> {
    override fun generateRules(): RuleSet<T>

    fun generateAllRules(): CoverageGroup<T> = FullCoverageGroup(generateRules())
}