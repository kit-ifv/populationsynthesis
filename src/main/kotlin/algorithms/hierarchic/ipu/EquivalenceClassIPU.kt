package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector
import edu.kit.ifv.populationsynthesis.algorithms.TargetNumberObserver
import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU
import edu.kit.ifv.populationsynthesis.algorithms.ipu.HistoricIPU
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.rules.toScalableVector
import edu.kit.ifv.populationsynthesis.utils.invertMap

class EquivalenceClassIPU<AREA, H>(
    ruleProvider: HierarchicRuleProvider<AREA, H>,
    seedHouseholds: Collection<H>,
    ipu: GenericIPU = GenericIPU.Companion.legacy,
) : HistoricIPU<AREA, H>(
    ruleProvider,
    seedHouseholds,
    ipu,
) {

    override fun generateEquivalenceClasses(
        rules: List<Rule<H>>,
        parentdropsize: Int
    ): Pair<Map<ScalableVector, List<H>>, List<TargetNumberObserver>> {
        // generate the vector mapping for each seed element.
        val vectors = seedHouseholds.associateWith { rules.toScalableVector(it) }
        // check which of those are identical by inverting the map and using the equality definition of ScalableVector
        val equivalenceClasses = vectors.invertMap()
        // The keys can be used as representatives
        val representatives = equivalenceClasses.keys
        val ruleObservers = rules.withIndex().drop(parentdropsize).map {
            RuleObserver.fromRule(it.value, it.index, representatives)
        }
        return equivalenceClasses to ruleObservers
    }
}