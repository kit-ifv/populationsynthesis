package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.rules.contribution.Contribution
import edu.kit.ifv.populationsynthesis.rules.contribution.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider

data class IndexedLogic<T>(
    val index: Int,
    val logicIdentifier: LogicIdentifier,
    val logic: Contribution<T>
) : Contribution<T> by logic {

    fun <AREA> toIndexedRule(target: AREA, ruleProvider: RuleProvider<AREA, T>): IndexedRule<T>? {
        val rule = ruleProvider[target, logicIdentifier] ?: return null
        return IndexedRule(index = index, rule = rule)
    }
}