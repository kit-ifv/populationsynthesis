package edu.kit.ifv.populationsynthesis.rules.composer

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.rules.Rule

class HierarchyComposer<AREA, H>(override val hierarchy: HierarchicElement<AREA>) : HierarchyRuleComposer<AREA, H> {
    override fun compose(target: AREA, rulesFor: (AREA) -> Collection<Rule<H>>): List<Rule<H>> {
        return hierarchy.getChildren(target).flatMap { rulesFor(it) }
    }
}