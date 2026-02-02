package examples.households

import edu.kit.ifv.populationsynthesis.rules.contribution.BooleanContributionDefinition

data class AgeContribution(
    val range: IntRange,
) : BooleanContributionDefinition<TestPerson>() {
    override fun evaluation(element: TestPerson): Boolean {
        return element.age in range
    }


    override fun generateDescription(): String = toString()
}