package edu.kit.ifv.populationsynthesis.domain.rules

import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.provider.ExhaustiveRuleProvider
import edu.kit.ifv.populationsynthesis.rules.provider.MapRuleProvider
import edu.kit.ifv.populationsynthesis.rules.provider.MutableExhaustiveRuleProvider
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider

abstract class CensusDataset<I, O>(protected val map: Map<ARSKey, I>) {
    protected fun buildExhaustiveProvider(extractor: (I) -> CoverageGroup<O>) : ExhaustiveRuleProvider<ARSKey, O> =
        MutableExhaustiveRuleProvider<ARSKey, O>().apply {
            map.forEach { (key, value) ->
                add(key, extractor(value))
            }
        }
    protected fun buildRuleProvider(extractor: (I) -> RuleSet<O>) : RuleProvider<ARSKey, O> =
        MapRuleProvider<ARSKey, O>().apply {
            map.forEach { (key, value) ->
                addRules(key, extractor(value))
            }
        }
    operator fun get(arsKey: ARSKey): I? {
        return map[arsKey]
    }
}