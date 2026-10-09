package edu.kit.ifv.populationsynthesis.synthesis

import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider

/**
 * If the ruleprovider is a hierarchic rule provider then the logic to determine the problem changes to the task
 * of additionally composing the rules, because subareas might have different rules registered, and since they
 * sift up to the target area in the hierarchic distribution algo we need to adapt the problem generation logic
 * likewisely.
 */
class HierarchicResourceTaskProvider<AREA, T>(
    seedElements: Collection<T>,
    override val ruleProvider: HierarchicRuleProvider<AREA, in T>,
    scalarInitializer: InitializeVectorScalars<T> = InitializeVectorScalars.byNormalizedElementCount
): ResourceTaskProvider<AREA, T>(seedElements, ruleProvider, scalarInitializer) {
    override fun provideProblemFor(area: AREA): MatrixTask {
        val rules = ruleProvider.getComposedRules(area)
        if (rules.isEmpty()) return MatrixTask.EMPTY
        return createTask(rules)
    }
}