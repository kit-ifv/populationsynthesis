package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.TargetNumberObserver
import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU
import edu.kit.ifv.populationsynthesis.rules.RuleLookup
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.rules.toScalableVector
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

    private lateinit var vectorMapping: Map<ScalableVector, T>
    override fun generateScalableVectors(area: AREA): Pair<Collection<ScalableVector>, List<TargetNumberObserver>> {
        val parents = hierarchy.getAllAncestors(area)
        val ruleLookup = RuleLookup.fromProvider(ruleProvider)

        val indexedRules = ruleLookup.getLogics(parents + area)

        val vectors = seedHouseholds.associateWith { indexedRules.map { it.rule }.toScalableVector(it) }
        vectorMapping = seedHouseholds.associateBy {indexedRules.map { it.rule }.toScalableVector(it)   }
        val observers = ruleLookup[area].map { (index, rule) ->
            RuleObserver.fromRule(rule, index, vectors.values)
        }
        return vectors.values to observers

    }

    override fun toElementRepresentations(vectors: ScalableVector): List<T> {
        val element = vectorMapping[vectors] ?: return emptyList()
        return listOf(element)
    }

    fun <K, V> identityHashMapOf(pairs: Collection<Pair<K, V>>): MutableMap<K, V> =
        IdentityHashMap<K, V>().apply {
            for ((k, v) in pairs) put(k, v)
        }

}