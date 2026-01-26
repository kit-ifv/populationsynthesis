package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import edu.kit.ifv.populationsynthesis.SampleAndCollect
import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.TargetNumberObserver
import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.synthesis.HierarchicSynthesis


abstract class HistoricIPU<AREA, T>(
    ruleProvider: HierarchicRuleProvider<AREA, T>,
    val seedHouseholds: Collection<T>,
    val ipu: GenericIPU = GenericIPU.Companion.legacy,
) : HierarchicSynthesis<AREA, T>(ruleProvider) {
    val extractor = SampleAndCollect<T>()
    override fun synthesize(
        highestArea: AREA,
        targetAreas: Collection<AREA>,
    ): Map<AREA, List<T>> {
        val vectors = calculate(highestArea, targetAreas)
        // Now the vectors should be scaled via side effect
        return vectors.entries.associate { (k, v) ->
            k to extractor.extract(v)
        }
    }

    abstract fun generateEquivalenceClasses(rules: Collection<Rule<T>>, parentdropsize: Int) :  Pair<Map<ScalableVector, List<T>>, List<TargetNumberObserver>>
    fun calculate(
        highestArea: AREA,
        targetAreas: Collection<AREA>,
    ): Map<AREA, Map<ScalableVector, List<T>>> {
        val parentRuleset = ruleProvider.getRules(highestArea)
        val areasToEquivalenceClasses = targetAreas.associateWith {
            val childRules = ruleProvider.getRules(it)
            val rules = parentRuleset + childRules
            generateEquivalenceClasses(rules, parentRuleset.size)
        }
        val (equivalenceClasses, observers) = areasToEquivalenceClasses.values.unzip()
        val allHouseholdsEncoded = equivalenceClasses.flatMap { it.keys }
        val additionalObservers = parentRuleset.withIndex().map {
            RuleObserver.fromRule(it.value, it.index, allHouseholdsEncoded)
        }

        ipu.run(allHouseholdsEncoded, additionalObservers + observers.flatten())
        // Abandons the observers. they are no longer required
        return areasToEquivalenceClasses.mapValues { it.value.first }
    }

}
