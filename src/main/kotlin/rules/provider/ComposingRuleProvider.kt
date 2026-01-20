package edu.kit.ifv.populationsynthesis.rules.provider

import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.composer.RuleComposer

/**
 * When we have a hierarchy of areas, we can calculate induced rules by checking the child elements and fusing the rules
 * from them. The rule composer tells us how we will form derived rules, that are not associated to the area.
 */
interface ComposingRuleProvider<AREA, T> : RuleProvider<AREA, T> {

    val composer: RuleComposer<AREA, T>

    fun getComposedRules(target: AREA): RuleSet<T> = composer.compose(target, ::getRules)
}

