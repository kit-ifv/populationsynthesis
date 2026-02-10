package edu.kit.ifv.populationsynthesis.statistics

import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.provider.HierarchicalRuleProviderLegacy
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider
import kotlin.math.abs
import kotlin.math.max

open class Evaluator<AREA, H>(open val ruleProvider: RuleProvider<AREA, H>) {

    open fun evaluate(output: Map<AREA, Collection<H>>): List<AreaIPUOutput<AREA>> {
        TODO()
    }
}

class HierarchicEvaluator<AREA, H>(override val ruleProvider: HierarchicalRuleProviderLegacy<AREA, H>) :
    Evaluator<AREA, H>(ruleProvider) {
    override fun evaluate(output: Map<AREA, Collection<H>>): List<AreaIPUOutput<AREA>> {
        val ruleMapping = ruleProvider.getAllRules()
        val ruleResults = ruleMapping.flatMap { (area, rules) ->

            val subareas =
                listOf(area) + (runCatching { ruleProvider.getAllDescendants(area) }.getOrNull() ?: emptyList())
            if (subareas.any { it in output }) {
                val currentHHs = subareas.flatMap {
                    output[it] ?: emptyList()
                }
                rules.toIPUOutput(area, currentHHs)
            } else {
                emptyList()
            }
        }
        return ruleResults
    }

}

fun <T, X> Collection<Rule<T>>.toIPUOutput(area: X, households: Collection<T>): List<AreaIPUOutput<X>> {
    return map { it.toIPUOutput(area, households) }
}

fun <T, X> Rule<T>.toIPUOutput(area: X, households: Collection<T>): AreaIPUOutput<X> {
    return AreaIPUOutput<X>(
        zone = area,
        IPUOutputLog(
            this.logic.identifier,
            target,
            total(households)
        )
    )
}

data class IPUOutputLog(
    val identifier: LogicIdentifier,
    val expected: Double,
    val actual: Double,

    ) {
    val difference: Double = expected - actual

    @Suppress("MagicNumber")
    val quotientDifference: Double = run {

        val exp = if (expected == 0.0) 1e-9 else expected
        val act = if (actual == 0.0) 1e-9 else actual
        max(exp / act, act / exp)
    }

    fun isImperfect() = expected != actual

    val percentDifference = abs(difference.toDouble()) / max(1.0, expected)
}

data class AreaIPUOutput<AREA>(
    val zone: AREA,
    val original: IPUOutputLog,
) {
    fun isImperfect() = original.isImperfect()

    val expected get() = original.expected
    val actual get() = original.actual
}
