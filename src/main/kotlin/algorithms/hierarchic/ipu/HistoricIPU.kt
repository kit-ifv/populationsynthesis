package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import edu.kit.ifv.populationsynthesis.SampleAndCollect
import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.TargetNumberObserver
import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.synthesis.HierarchicSynthesis
import edu.kit.ifv.populationsynthesis.utils.EquivalenceClass


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
    abstract fun generateScalableVectors(area: AREA) : Pair<Collection<ScalableVector>, List<TargetNumberObserver>>
    abstract fun generateEquivalenceClasses(rules: Collection<Rule<T>>, parentdropsize: Int) :  Pair<EquivalenceClass<ScalableVector, T>, List<TargetNumberObserver>>
    fun calculate(
        highestArea: AREA,
        targetAreas: Collection<AREA>, // The lowest areas are the target areas. They must generate scalable vectors.
    ): Map<AREA, EquivalenceClass<ScalableVector, T>> {
        // And their equivalence class defintion is purely dependent on the rule definition of their parents
        // Basically, each area needs to spawn the rule observers of the rules that reside in its level
        // Question: Can we use different equivalence classes when some inheritance chain has a smaller rule set?

        val parents = (hierarchy.getAllChildren(highestArea) + highestArea).filter { !hierarchy.isLeaf(it) }
        val parentRuleset = ruleProvider.getRules(highestArea)

        val leafsToScalableVectors = targetAreas.associateWith { target ->
            generateScalableVectors(target)
        }
        val ltSc = leafsToScalableVectors.mapValues { it.value.first }
        val ltObs = leafsToScalableVectors.mapValues { it.value.second }
        val areasToEquivalenceClasses = targetAreas.associateWith {
            val childRules = ruleProvider.getRules(it)
            val rules = parentRuleset + childRules
            generateEquivalenceClasses(rules, parentRuleset.size)
        }
        val (equivalenceClasses, observers) = areasToEquivalenceClasses.values.unzip()

        val parentObservers = parents.map { parent ->
            val relevantLeafs = hierarchy.getAllChildren(parent).filter { it in targetAreas }
            val vectors = relevantLeafs.mapNotNull {ltSc[it]}
            RuleObserverBuilder(ruleProvider).buildFromVectors(parent, vectors.flatten())
        }
        // Here the generation of households should happen for each non leaf node, and the observer should only get the
        // equivalence class definitions from the leaf areas that are connected to this parent area.
        val allHouseholdsEncoded = equivalenceClasses.flatMap { it.keys }
        val additionalObservers = parentRuleset.withIndex().map { (index, rule) ->
            RuleObserver.fromRule(rule, index, allHouseholdsEncoded)
        }

        ipu.run(allHouseholdsEncoded, additionalObservers + observers.flatten())
        // Abandons the observers. they are no longer required
        return areasToEquivalenceClasses.mapValues { it.value.first }
    }



}
