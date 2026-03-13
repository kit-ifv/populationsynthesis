package edu.kit.ifv.populationsynthesis.rules

import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.measurement.Measurement
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider

data class IndexedLogic<T>(
    val index: Int,
    val logicIdentifier: LogicIdentifier,
    val logic: Measurement<T>
) : Measurement<T> by logic {

    fun <AREA> toIndexedRule(target: AREA, ruleProvider: RuleProvider<AREA, T>): IndexedRule<T>? {
        val rule = ruleProvider[target, logicIdentifier] ?: return null
        return IndexedRule(index = index, rule = rule)
    }
}