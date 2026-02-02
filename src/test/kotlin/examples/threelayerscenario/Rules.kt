package examples.threelayerscenario

import edu.kit.ifv.populationsynthesis.rules.ExhaustiveRuleGenerator
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.contribution.BooleanContributionDefinition
import edu.kit.ifv.populationsynthesis.rules.contribution.ContributionDefinition
import edu.kit.ifv.populationsynthesis.rules.provider.MapRuleProvider
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider
import edu.kit.ifv.populationsynthesis.rules.toRuleSet

class HelpGenerator(code: String, val yes: Int, val no: Int, val elementor: (SeedElement) -> Boolean) :
    ExhaustiveRuleGenerator<SeedElement> {
    val contributors = descriptors(code, elementor)
    override fun generateRules(): RuleSet<SeedElement> {
        return contributors.map { it.createNamedContribution() }.zip(listOf(yes, no)).map { (desc, target) ->
            desc.withTarget(target.toDouble())
        }.toRuleSet()
    }

    companion object {
        fun A(yes: Int, no: Int) = HelpGenerator("A", yes, no) {
            it.a
        }

        fun B(yes: Int, no: Int) = HelpGenerator("B", yes, no) {
            it.b
        }

        fun C(yes: Int, no: Int) = HelpGenerator("C", yes, no) {
            it.c
        }
    }
}

fun descriptors(code: String, lambda: (SeedElement) -> Boolean): List<ContributionDefinition<SeedElement>> {
    return listOf(YesDescriptor(code, lambda), NoDescriptor(code, lambda))
}

class YesDescriptor(val code: String, val lambda: (SeedElement) -> Boolean) :
    BooleanContributionDefinition<SeedElement>() {
    override fun generateDescription(): String {
        return "YesDescriptor($code)"
    }

    override fun evaluation(element: SeedElement): Boolean {
        return lambda(element)
    }
}

class NoDescriptor(val code: String, val lambda: (SeedElement) -> Boolean) :
    BooleanContributionDefinition<SeedElement>() {
    override fun generateDescription(): String {
        return "NoDescriptor($code)"

    }

    override fun evaluation(element: SeedElement): Boolean {
        return !lambda(element)
    }
}

internal val ABCRuleProvider: RuleProvider<Area, SeedElement> = MapRuleProvider<Area, SeedElement>().apply {
    addRules(C.C1, HelpGenerator.C(16, 16))
    addRules(B.B1, HelpGenerator.B(12, 12))
    addRules(B.B2, HelpGenerator.B(4, 4))
    addRules(A.A1, HelpGenerator.A(5, 5))
    addRules(A.A2, HelpGenerator.A(7, 7))
    addRules(A.A3, HelpGenerator.A(1, 2))
    addRules(A.A4, HelpGenerator.A(4, 3))
}