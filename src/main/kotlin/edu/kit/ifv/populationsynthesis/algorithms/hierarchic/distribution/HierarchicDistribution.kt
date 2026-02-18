package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.GenericCollector
import edu.kit.ifv.populationsynthesis.SignatureOld
import edu.kit.ifv.populationsynthesis.Signature
import edu.kit.ifv.populationsynthesis.algorithms.IntegerIPUOutput
import edu.kit.ifv.populationsynthesis.rules.LogicIndexer
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicRuleProvider
import edu.kit.ifv.populationsynthesis.standardRoundingStrategy
import edu.kit.ifv.populationsynthesis.synthesis.HierarchicSynthesis

class HierarchicDistribution<AREA, T>(
    ruleProvider: HierarchicRuleProvider<AREA, T>,
    val seedHouseholds: Collection<T>,
    val config: HierarchicDistributionConfig = HierarchicDistributionConfig()) : HierarchicSynthesis<AREA, T>(ruleProvider) {



    private val allRuleLogics = LogicIndexer.fromProvider(ruleProvider)
    private val householdMapping = initializeHouseholdMapping()

    private fun initializeHouseholdMapping(): Map<SignatureOld, List<T>> {
        return seedHouseholds.groupBy { element ->
            allRuleLogics.allMeasurements().withIndex().map { (index, logic) ->
                index to logic.measure(element)
            }.filter { it.second != 0.0 }.toMap()
        }
    }

    /**
     * Generate a solution of the amount of households (read signatures for efficiency) which then can be distributed
     * According to whatever other strategy is present.
     */
    fun initialSolution(target: AREA): List<SignatureAmount> {
        val rules = ruleProvider.getComposedRules(target)
        // TODO What if there are different rules provided at different root areas, they cannot be traced back to their original signture created from the logic indexer.
        val oIpu = config.ipu.calculateSignature(seedHouseholds, rules, config.ipuCalculationCallback)



        val integerIPUResult = standardRoundingStrategy.integerizeIPUOutput(oIpu)

//        rules.verify(integerIPUResult)
        // TODO would be nice to see how much the integerization causes the initial solution quality to drop.
        val sigs = integerIPUResult.map { (element, amount) ->
            SignatureAmount(element, amount)
        }



        return sigs
    }

    override fun synthesize(
        highestArea: AREA,
        targetAreas: Collection<AREA>,
    ): Map<AREA, List<T>> {

        val initialSolution = initialSolution(highestArea)
        val logicIndexer = allRuleLogics.filter { it in targetAreas + highestArea }
        val distributor = OriginalDistributor(ruleProvider,logicIndexer, seedHouseholds)
        return distributor.distribute(
            initialSolution,
            highestArea,
        )
    }

    private val collector = GenericCollector<SignatureAmount, T> {
        it.map { it.amount }
    }

    fun finalize(
        amounts: Collection<SignatureAmount>,
        signatures: Map<Signature, List<T>>,
    ): List<T> {
        val targetMap = amounts.associateWith {
            val targetHouseholds = signatures[it.signature] ?: run {
                error("No households for the signature ${it.signature}")
            }
            targetHouseholds
        }
        return collector.extract(targetMap).map { it }
    }





}

fun Collection<Rule<*>>.verify(target: Collection<IntegerIPUOutput<SignatureOld>>) {
    withIndex().forEach { (index, rule) ->
        val amount = target.sumOf { it.amount * (it.element[index]?: 0.0) }
        val output = "${rule.description} target=${rule.target} amount=$amount"
        println(output)
    }
}
