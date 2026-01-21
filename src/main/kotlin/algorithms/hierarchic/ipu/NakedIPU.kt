package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.TargetNumberObserver
import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU
import edu.kit.ifv.populationsynthesis.algorithms.ipu.HistoricIPU
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.rules.toScalableVector
import java.util.IdentityHashMap

class NakedIPU<AREA, H>(
    ruleProvider: HierarchicRuleProvider<AREA, H>,
    seedHouseholds: Collection<H>,
    ipu: GenericIPU = GenericIPU.Companion.legacy,
): HistoricIPU<AREA, H>(
    ruleProvider,
seedHouseholds,
ipu,

    ) {
    override fun generateEquivalenceClasses(rules: Collection<Rule<H>>, parentdropsize: Int):  Pair<Map<ScalableVector, List<H>>, List<TargetNumberObserver>> {
        val vectors = seedHouseholds.associateWith { rules.toScalableVector(it) }
        val ruleObservers = rules.withIndex().drop(parentdropsize).map {
            RuleObserver.fromRule(it.value, it.index, vectors.values)
        }

        val pairs = vectors.values.zip(seedHouseholds).map { (a, b) ->
            a to listOf(b)
        }
        val mapping = identityHashMapOf(pairs)

        return mapping to ruleObservers
    }

    fun <K, V> identityHashMapOf(pairs: Collection<Pair<K, V>>): MutableMap<K, V> =
        IdentityHashMap<K, V>().apply {
            for ((k, v) in pairs) put(k, v)
        }

}