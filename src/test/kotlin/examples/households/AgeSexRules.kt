package examples.households

import edu.kit.ifv.populationsynthesis.rules.contribution.BooleanContributionDefinition

data class AgeSexRule(
    val lowerbound: Int,
    val upperbound: Int,
    val sex: Boolean,

    ) : BooleanContributionDefinition<TestPerson>() {
    override fun evaluation(element: TestPerson): Boolean {
        return element.age in lowerbound..upperbound && element.sex == sex
    }


    override fun generateDescription(): String = toString()
}
