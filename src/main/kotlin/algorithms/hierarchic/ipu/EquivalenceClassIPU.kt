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
import edu.kit.ifv.populationsynthesis.utils.formEquivalenceClass
import edu.kit.ifv.populationsynthesis.utils.invertMap

class EquivalenceClassIPU<AREA, T>(
    ruleProvider: HierarchicRuleProvider<AREA, T>,
    seedHouseholds: Collection<T>,
    ipu: GenericIPU = GenericIPU.Companion.legacy,
) : HistoricIPU<AREA, T>(
    ruleProvider,
    seedHouseholds,
    ipu,
) {

    override fun generateEquivalenceClasses(
        rules: Collection<Rule<T>>,
        parentdropsize: Int
    ): Pair<EquivalenceClass<ScalableVector, T>, List<TargetNumberObserver>> {
        // generate the vector mapping for each seed element.
        val vectors = seedHouseholds.associateWith { rules.toScalableVector(it) }
        // check which of those are identical by inverting the map and using the equality definition of ScalableVector
        val equivalenceClasses = vectors.invertMap()
        // The keys can be used as representatives
        val representatives = equivalenceClasses.keys
        val ruleObservers = rules.withIndex().drop(parentdropsize).map { (index, rule) ->
            RuleObserver.fromRule(rule, index, representatives)
        }
        return MapBasedEquivalenceClass(equivalenceClasses) to ruleObservers
    }

    override fun toHouseholds(vectors: ScalableVector): List<T> {
        TODO("Not yet implemented")
    }

    override fun generateScalableVectors(area: AREA): Pair<Collection<ScalableVector>, List<TargetNumberObserver>> {
        val parents = hierarchy.getAllAncestors(area)
        val ruleLookup = RuleLookup.fromProvider(ruleProvider)

        val indexedRules = ruleLookup.getLogics(parents)

        val equivalenceClasses = seedHouseholds.associateWith { indexedRules.map { it.rule }.toScalableVector(it) }.formEquivalenceClass()

        val representatives = equivalenceClasses.representatives
        val observers = ruleLookup[area].map { (index, rule) ->
            RuleObserver.fromRule(rule, index, representatives)
        }


        return equivalenceClasses.representatives to observers

    }
}

