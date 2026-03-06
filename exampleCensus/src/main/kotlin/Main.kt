package edu.kit.ifv.populationsynthesis

import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.HierarchicDistribution
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.HierarchicDistributionConfig
import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU
import edu.kit.ifv.populationsynthesis.domain.area.HierarchyFactory
import edu.kit.ifv.populationsynthesis.domain.rules.RuleProviderFactory
import edu.kit.ifv.populationsynthesis.domain.population.Population
import edu.kit.ifv.populationsynthesis.input.writeCsv
import edu.kit.ifv.populationsynthesis.evaluation.Verification
import edu.kit.ifv.populationsynthesis.output.CensusHouseholdConverter
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val hierarchy = HierarchyFactory.marneExample()
    val rules = RuleProviderFactory.marneExample()
    val hierarchicRuleProvider = rules.withHierarchy(hierarchy)
    val population = Population.fromPersonInfo()


    val ipu = HierarchicDistribution(
        ruleProvider = hierarchicRuleProvider,
        seedHouseholds = population.households,
        config = HierarchicDistributionConfig(
            // You can use different algorithms for the initial solution of the distribution algorithm.
//            ipu = LeastSquareRegression,
//            ipu = SparkNonnegativeLeastSquares,
//            ipu = TabooListIPU(blockAmount = 5, iterations = 1000),
//            ipu = MaximumAnnihilator(),
            ipu = GenericIPU.legacy
        )
    )
    // You can also use the traditional hierarchic IPU of Konduri instead of the Distribution Algorithm.
//    val ipu = EquivalenceClassIPU(
//        ruleProvider = hierarchicRuleProvider,
//        seedHouseholds = population.households,
//    )

    val output = ipu.synthesizeAll()
    val verificationOutput = Verification.verify(hierarchicRuleProvider, output)
    writeCsv(Path("output/Example.csv").createParentDirectories(), verificationOutput)
    writeCsv(Path("output/Population.csv").createParentDirectories(), CensusHouseholdConverter.convert(output))
    println("DONE")

}