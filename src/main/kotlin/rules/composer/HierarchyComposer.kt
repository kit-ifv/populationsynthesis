package edu.kit.ifv.populationsynthesis.rules.composer

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.sumRule

class HierarchyComposer<AREA, T>(override val hierarchy: HierarchicElement<AREA>) : HierarchyRuleComposer<AREA, T> {
    override fun compose(target: AREA, rulesFor: (AREA) -> Collection<Rule<T>>): RuleSet<T> {
        return RuleSet.create(
            rules = hierarchy.getAllChildren(target).flatMap{ rulesFor(it)},
            accumulator = Collection<Rule<T>>::sumRule
        )
    }
}