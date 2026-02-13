package edu.kit.ifv.populationsynthesis.domain.rules

import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.provider.ExhaustiveRuleProvider
import edu.kit.ifv.populationsynthesis.rules.provider.MutableExhaustiveRuleProvider

abstract class CensusDataset<I, O>(protected val map: Map<ARSKey, I>) {
    protected fun buildExhaustiveProvider(extractor: (I) -> CoverageGroup<O>) : ExhaustiveRuleProvider<ARSKey, O> =
        MutableExhaustiveRuleProvider<ARSKey, O>().apply {
            map.forEach { (key, value) ->
                add(key, extractor(value))
            }
        }

    operator fun get(arsKey: ARSKey): I? {
        return map[arsKey]
    }
}