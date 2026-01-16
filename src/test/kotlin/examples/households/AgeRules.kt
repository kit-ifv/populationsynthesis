package examples.households

import edu.kit.ifv.populationsynthesis.rules.contribution.NamedContribution
import edu.kit.ifv.populationsynthesis.rules.covered.ExhaustiveContributionSetSupplier
import edu.kit.ifv.populationsynthesis.rules.covered.FullDescriptorGroup

class AgeRules(private val ageThresholds: List<Int>) : ExhaustiveContributionSetSupplier<TestPerson> {

    constructor(vararg ageThresholds: Int) : this(ageThresholds.toList())

    override fun generateAllDescriptions(): FullDescriptorGroup<TestPerson> {
        val contributions = listOf(AgeContribution(0..ageThresholds.first())) +
                ageThresholds.zipWithNext { a, b ->
                    AgeContribution(a..b)
                } + AgeContribution(ageThresholds.last()..Int.MAX_VALUE)
        return FullDescriptorGroup(contributions.map { it.createNamedContribution() })
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AgeRules) return false
        return ageThresholds == other.ageThresholds
    }

    override fun hashCode(): Int {
        return ageThresholds.hashCode()
    }

    override fun generateContributions(): List<NamedContribution<TestPerson>> {
        TODO("Not yet implemented")
    }
}

class AgeRuleBuilder(
    private val thresholds: MutableList<Int>
) {
    fun build() {

    }
}