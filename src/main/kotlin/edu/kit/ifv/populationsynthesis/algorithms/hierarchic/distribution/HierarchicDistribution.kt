package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.datastructures.Signature
import edu.kit.ifv.populationsynthesis.rules.LogicIndexer
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.synthesis.HierarchicResourceTaskProvider
import edu.kit.ifv.populationsynthesis.synthesis.HierarchicSynthesis
import edu.kit.ifv.populationsynthesis.synthesis.MatrixTaskProvider

class HierarchicDistribution<AREA, T>(
    ruleProvider: HierarchicRuleProvider<AREA, in T>,
    val seedElements: Collection<T>,
    val matrixTaskProvider: MatrixTaskProvider<AREA, T> = HierarchicResourceTaskProvider(seedElements, ruleProvider),
    val config: HierarchicDistributionConfig = HierarchicDistributionConfig()
) : HierarchicSynthesis<AREA, T>(ruleProvider) {


    private val allRuleLogics = LogicIndexer.fromProvider(ruleProvider)
    private val householdMapping = initializeElementMapping()
    init {
        require(householdMapping.keys.none { it.isEmpty() }) {
            "This is not a valid signature state"
        }
    }
    private fun initializeElementMapping(): Map<Signature, List<T>> {
        val groupBy: Map<Signature?, List<T>> = seedElements.groupBy { element ->
            allRuleLogics.createSignature(element)


        }
        if(null in groupBy) {
            println("There are ${groupBy[null]!!.size} households that do not match to a signature. Be advised that these households are not used. ")
        }
        return groupBy.filterKeys { it != null } as Map<Signature, List<T>>
    }

    /**
     * Generate a solution of the amount of households (read signatures for efficiency) which then can be distributed
     * According to whatever other strategy is present.
     */
    fun initialSolution(target: AREA): List<SignatureAmount> {
        val matrixTask = matrixTaskProvider[target]

        config.matrixSolver.solve(matrixTask)
        val matrixSolution = matrixTask.extractSolution()
        val integerMatrixResult = config.roundingStrategy.integerizeMatrixTask(matrixTask)
        val sigs = integerMatrixResult.map { (element, amount) ->
            SignatureAmount(element, amount)
        }



        return sigs
    }

    override fun synthesize(
        highestArea: AREA,
        targetAreas: Collection<AREA>,
    ): Map<AREA, List<T>> {

        val initialSolution = initialSolution(highestArea)
        val distributor = OriginalDistributor(ruleProvider, allRuleLogics, householdMapping, config.samplerAlgorithm)
        return distributor.distribute(
            initialSolution,
            highestArea,
        )
    }


}

