package edu.kit.ifv.populationsynthesis

import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.HierarchicDistribution
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.HierarchicDistributionConfig
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu.EquivalenceClassIPU
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.ipu.NakedIPU
import edu.kit.ifv.populationsynthesis.algorithms.ipu.EvilLSQRIPU
import edu.kit.ifv.populationsynthesis.algorithms.ipu.GenericIPU
import edu.kit.ifv.populationsynthesis.algorithms.ipu.MaximumAnnihilator
import edu.kit.ifv.populationsynthesis.algorithms.ipu.SparkNNLS
import edu.kit.ifv.populationsynthesis.algorithms.ipu.TabooListIPU
import edu.kit.ifv.populationsynthesis.datasource.HierarchyFactory
import edu.kit.ifv.populationsynthesis.datasource.RuleProviderFactory
import edu.kit.ifv.populationsynthesis.evaluation.Verification

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val hierarchy = HierarchyFactory.marneExample()
    val rules = RuleProviderFactory.marneExample()
    val hierarchicRuleProvider = rules.withHierarchy(hierarchy)
    val population = Population.fromPersonInfo()


    val newIpu = HierarchicDistribution(hierarchicRuleProvider, population.households,
//        HierarchicDistributionConfig(ipu = TabooListIPU(blockAmount = 5, iterations =  1000)
        HierarchicDistributionConfig(ipu = GenericIPU.legacy

        )
    )

    val ipu = EquivalenceClassIPU(hierarchicRuleProvider, population.households)

//    val output = ipu.synthesizeAll()
    val output = newIpu.synthesizeAll()
    val verificationOutput = Verification().verify(hierarchicRuleProvider, output)

    println("DONE")

}