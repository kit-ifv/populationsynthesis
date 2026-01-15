package examples.households

import edu.kit.ifv.populationsynthesis.rules.NamedContribution
import edu.kit.ifv.populationsynthesis.rules.covered.ExhaustiveContributionGenerator
import edu.kit.ifv.populationsynthesis.rules.covered.FullDescriptorGroup

class AgeRules(private val ageThresholds: List<Int>) : ExhaustiveContributionGenerator<TestPerson> {

    constructor(vararg ageThresholds: Int) : this(ageThresholds.toList())
    override val description: String = "Age groups by $ageThresholds"

    override fun generateAllDescriptions(): FullDescriptorGroup<TestPerson> {
        val contributions = listOf(AgeContribution(0..ageThresholds.first())) +
                ageThresholds.zipWithNext { a, b ->
                    AgeContribution(a..b)
                } + AgeContribution(ageThresholds.last()..Int.MAX_VALUE)
        return FullDescriptorGroup(contributions.map { it.generateDescription() })
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AgeRules) return false
        return ageThresholds == other.ageThresholds
    }

    override fun hashCode(): Int {
        return ageThresholds.hashCode()
    }

    override fun generateDescriptions(): List<NamedContribution<TestPerson>> {
        TODO("Not yet implemented")
    }
}

class AgeRuleBuilder(
    private val thresholds: MutableList<Int>
) {
    fun build() {

    }
}