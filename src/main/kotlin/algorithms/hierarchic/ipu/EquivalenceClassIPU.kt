package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.TargetNumberObserver
import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU
import edu.kit.ifv.populationsynthesis.rules.RuleLookup
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.rules.toScalableVector
import edu.kit.ifv.populationsynthesis.utils.formEquivalenceClass

class EquivalenceClassIPU<AREA, T>(
    ruleProvider: HierarchicRuleProvider<AREA, T>,
    seedHouseholds: Collection<T>,
    ipu: GenericIPU = GenericIPU.Companion.legacy,
) : HistoricIPU<AREA, T>(
    ruleProvider,
    seedHouseholds,
    ipu,
) {

    override fun toElementRepresentations(vectors: ScalableVector): List<T> {
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

