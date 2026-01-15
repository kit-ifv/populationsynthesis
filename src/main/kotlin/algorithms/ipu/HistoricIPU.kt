package edu.kit.ifv.populationsynthesis.algorithms.ipu

import edu.kit.ifv.populationsynthesis.SampleAndCollect
import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.TargetNumberObserver
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.rules.toScalableVector
import edu.kit.ifv.populationsynthesis.synthesis.HierarchicSynthesis
import edu.kit.ifv.populationsynthesis.utils.invertMap
import java.util.*

fun <K, V> identityHashMapOf(pairs: Collection<Pair<K, V>>): MutableMap<K, V> =
    IdentityHashMap<K, V>().apply {
        for ((k, v) in pairs) put(k, v)
    }

class HistoricIPU<AREA, H>(
    ruleProvider: HierarchicRuleProvider<AREA, H>,
    val seedHouseholds: Collection<H>,
    val ipu: GenericIPU = GenericIPU.legacy,
    val collapseEquivalents: Boolean = false,
) : HierarchicSynthesis<AREA, H>(ruleProvider) {
    val extractor = SampleAndCollect<H>()
    override fun synthesize(
        highestArea: AREA,
        targetAreas: Collection<AREA>,
    ): Map<AREA, List<H>> {
        val vectors = calculate(highestArea, targetAreas)
        // Now the vectors should be scaled via side effect
        return vectors.entries.associate { (k, v) ->
            k to extractor.extract(v)
        }
    }

    fun  generateMapping(rules: List<Rule<H>>, parentdropsize: Int):  Pair<Map<ScalableVector, List<H>>, List<TargetNumberObserver>> {
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

    fun generateStrongerMapping(rules: List<Rule<H>>, parentdropsize: Int):  Pair<Map<ScalableVector, List<H>>, List<TargetNumberObserver>> {
        val vectors = seedHouseholds.associateWith { rules.toScalableVector(it) }
        val inverseMap = vectors.invertMap()
        val uniqueVectors = inverseMap.keys
        val ruleObservers = rules.withIndex().drop(parentdropsize).map {
            RuleObserver.fromRule(it.value, it.index, uniqueVectors)
        }
        return inverseMap to ruleObservers
    }

    fun calculate(
        highestArea: AREA,
        targetAreas: Collection<AREA>,
    ): Map<AREA, Map<ScalableVector, List<H>>> {
        val parentRuleset = ruleProvider.getRules(highestArea)
        val temp = targetAreas.associateWith {
            val childRules = ruleProvider.getRules(it)
            val rules = parentRuleset + childRules

            val b = if(this.collapseEquivalents) generateStrongerMapping(rules, parentRuleset.size) else generateMapping(rules, parentRuleset.size)
            b
        }
        val (vectors, observers) = temp.values.unzip()
        val allHouseholdsEncoded = vectors.map { it.keys }.flatten()
        val additionalObservers = parentRuleset.withIndex().map {
            RuleObserver.Companion.fromRule(it.value, it.index, allHouseholdsEncoded)
        }

        ipu.run(allHouseholdsEncoded, additionalObservers + observers.flatten())
        return temp.mapValues { it.value.first }
    }

    override fun synthesizeAll(): Map<AREA, List<H>> {
        TODO("Not yet implemented")
    }
}
