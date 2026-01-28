package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.TargetNumberObserver
import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleLookup
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.rules.toScalableVector
import edu.kit.ifv.populationsynthesis.utils.EquivalenceClass
import edu.kit.ifv.populationsynthesis.utils.MapBasedEquivalenceClass
import java.util.*

class NakedIPU<AREA, T>(
    ruleProvider: HierarchicRuleProvider<AREA, T>,
    seedHouseholds: Collection<T>,
    ipu: GenericIPU = GenericIPU.Companion.legacy,
): HistoricIPU<AREA, T>(
    ruleProvider,
seedHouseholds,
ipu,

    ) {
    override fun generateScalableVectors(area: AREA): Pair<Collection<ScalableVector>, List<TargetNumberObserver>> {
        val parents = hierarchy.getAllAncestors(area)
        val ruleLookup = RuleLookup.fromProvider(ruleProvider)

        val indexedRules = ruleLookup.getLogics(parents + area)

        val vectors = seedHouseholds.associateWith { indexedRules.map { it.rule }.toScalableVector(it) }
        val observers = ruleLookup[area].map { (index, rule) ->
            RuleObserver.fromRule(rule, index, vectors.values)
        }
        return vectors.values to observers

    }

    override fun generateEquivalenceClasses(rules: Collection<Rule<T>>, parentdropsize: Int):  Pair<EquivalenceClass<ScalableVector, T>, List<TargetNumberObserver>> {
        val vectors = seedHouseholds.associateWith { rules.toScalableVector(it) }
        val ruleObservers = rules.withIndex().drop(parentdropsize).map {
            RuleObserver.fromRule(it.value, it.index, vectors.values)
        }

        val pairs = vectors.values.zip(seedHouseholds).map { (a, b) ->
            a to listOf(b)
        }
        val mapping = identityHashMapOf(pairs)

        return MapBasedEquivalenceClass(mapping) to ruleObservers
    }

    fun <K, V> identityHashMapOf(pairs: Collection<Pair<K, V>>): MutableMap<K, V> =
        IdentityHashMap<K, V>().apply {
            for ((k, v) in pairs) put(k, v)
        }

}