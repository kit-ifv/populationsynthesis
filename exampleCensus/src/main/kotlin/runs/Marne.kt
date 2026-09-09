package edu.kit.ifv.populationsynthesis.runs

import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.HierarchicDistribution
import edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution.HierarchicDistributionConfig
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.ipu.CyclicIPU
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.MatrixSolver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.toVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.ScalableVector
import edu.kit.ifv.populationsynthesis.datastructures.linalg.sum
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
import edu.kit.ifv.populationsynthesis.utils.NumericProbabilityDistribution
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.math.max

fun main() {
    val runConfig = RunConfig(
        scenario = Scenario.MARNE,
        matrixSolver = CyclicIPU(),
        outputPath = Path("output/PopulationMarne.csv"),
        verificationPath = Path("output/ExampleMarne.csv"),

        )

    runConfig.execute()


}
typealias MatrixSolutionCallback = (Collection<ScalableVector>, Collection<RuleObserver>) -> Unit
typealias InitialVectorCallback = (List<ScalableVector>) -> Unit

data class RunConfig(
    var scenario: Scenario = Scenario.MARNE,
    var matrixSolver: MatrixSolver = CyclicIPU(),
    var population: Population = Population.fromPersonInfo(),
    var verificationPath: Path? = null,
    var outputPath: Path? = null,
    var initialVectorCallback: InitialVectorCallback = {},
    var matrixSolutionCallback: MatrixSolutionCallback = PredefinedCallbacks.PRINT::callback
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
                initialVectorCallback = initialVectorCallback,
                matrixSolutionCallback = matrixSolutionCallback
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

class SolutionWriteToFile(val path: Path) {
    private data class Write(
        val alpha: Double,
        val normalizedAlpha: Double,
        val originalAlpha: Double,
        val spread: Double = originalAlpha / max(normalizedAlpha, 1e-9)
    )

    private var originalScalars = listOf<Double>()
    fun register(vectors: List<ScalableVector>) {
        originalScalars = vectors.map { it.scalar }
    }

    fun write(vectors: Collection<ScalableVector>, observers: Collection<RuleObserver>) {
        val sum = vectors.sumOf { it.scalar }
        val output = vectors.zip(originalScalars).map { (it, o) ->
            Write(it.scalar, it.scalar / sum, o)
        }
        val original = NumericProbabilityDistribution.fromWeights(originalScalars)
        val newScalars = NumericProbabilityDistribution.fromWeights(vectors.map { it.scalar })
        println(original.kullbackLeiblerDivergence(newScalars))
        println(newScalars.kullbackLeiblerDivergence(original))
        writeCsv(path, output)

    }
}

enum class PredefinedCallbacks {
    NONE {
        override fun callback(
            vectors: Collection<ScalableVector>,
            observers: Collection<RuleObserver>
        ) {
        }
    },
    PRINT {
        override fun callback(
            vectors: Collection<ScalableVector>,
            observers: Collection<RuleObserver>
        ) {
            val message = vectors.sum()
            val target = observers.toVector()
            val diff = target - message
            println()
            println("-----Matrix Solver Print -----")
            println("target:      $target")
            println("Vector vSum: $message")
            println("delta:       $diff")
            println("||t - vSum||2 = ${diff.length()}")
//            println(observers.map { it.absoluteDifference })
//            println(observers.map { it.percentError })
//            println("-----Vector information ------")
//            val sum = vectors.sumOf { it.scalar }
//            val text = vectors.map { it.scalar / sum }
//            println(text)
//            println(text.min())
//            println(text.max())
            println("----- /* End Matrix Sl*/ -----")
            println()
        }
    };

    abstract fun callback(vectors: Collection<ScalableVector>, observers: Collection<RuleObserver>)
}

enum class Scenario {
    MARNE() {
        override fun spawnHierarchy(): HierarchicElement<ARSKey> = HierarchyFactory.marneExample()
        override fun spawnRuleProvider(): RuleProvider<ARSKey, CensusHousehold> = RuleProviderFactory.marneExample()
    },
    BAVARIA() {
        override fun spawnHierarchy(): HierarchicElement<ARSKey> {
            return HierarchyFactory.fromARSKeyset(spawnRuleProvider().getAllRules().keys)
        }

        override fun spawnRuleProvider(): RuleProvider<ARSKey, CensusHousehold> {
            return RuleProviderFactory.bavaria()
        }

    };

    abstract fun spawnHierarchy(): HierarchicElement<ARSKey>
    abstract fun spawnRuleProvider(): RuleProvider<ARSKey, CensusHousehold>

}