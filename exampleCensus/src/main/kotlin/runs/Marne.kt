package edu.kit.ifv.populationsynthesis.runs

import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.HierarchicDistribution
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.HierarchicDistributionConfig
import edu.kit.ifv.populationsynthesis.algorithms.ipu.CyclicIPU
import edu.kit.ifv.populationsynthesis.algorithms.ipu.JosephineStepper
import edu.kit.ifv.populationsynthesis.algorithms.ipu.MatrixSolver
import edu.kit.ifv.populationsynthesis.domain.area.ARSKey
import edu.kit.ifv.populationsynthesis.domain.area.HierarchyFactory
import edu.kit.ifv.populationsynthesis.domain.population.CensusHousehold
import edu.kit.ifv.populationsynthesis.domain.population.Population
import edu.kit.ifv.populationsynthesis.domain.rules.RuleProviderFactory
import edu.kit.ifv.populationsynthesis.evaluation.Verification
import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.input.writeCsv
import edu.kit.ifv.populationsynthesis.output.CensusHouseholdConverter
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories

fun main() {
    val runConfig = RunConfig(
        scenario = Scenario.MARNE,
        matrixSolver = CyclicIPU(),
        outputPath = Path("output/PopulationMarne.csv"),
        verificationPath = Path("output/ExampleMarne.csv")

    )

    runConfig.execute()


}

data class RunConfig(
    var scenario: Scenario = Scenario.MARNE,
    var matrixSolver: MatrixSolver = CyclicIPU(),
    var population: Population = Population.fromPersonInfo(),
    var verificationPath: Path? = null,
    var outputPath: Path? = null,
) {
    fun execute(lambda: RunConfig.() -> Unit) {
        apply(lambda)
        execute()
    }
    fun execute() {
        val hierarchy = scenario.spawnHierarchy()
        val rules = scenario.spawnRuleProvider()
        val hierarchicRuleProvider = rules.withHierarchy(hierarchy)

        val algorithm = HierarchicDistribution(
            ruleProvider = hierarchicRuleProvider,
            seedHouseholds = population.households,
            config = HierarchicDistributionConfig(
                matrixSolver = matrixSolver,
            )
        )

        val output = algorithm.synthesizeAll()
        verificationPath?.let {
            val verificationOutput = Verification.verify(hierarchicRuleProvider, output)
            writeCsv(it.createParentDirectories(), verificationOutput)
        }
        outputPath?.let {
            writeCsv(it.createParentDirectories(), CensusHouseholdConverter.convert(output))
        }
    }
}


enum class Scenario {
    MARNE() {
        override fun spawnHierarchy(): HierarchicElement<ARSKey> = HierarchyFactory.marneExample()
        override fun spawnRuleProvider(): RuleProvider<ARSKey, CensusHousehold> = RuleProviderFactory.marneExample()
    };

    abstract fun spawnHierarchy(): HierarchicElement<ARSKey>
    abstract fun spawnRuleProvider(): RuleProvider<ARSKey, CensusHousehold>

}