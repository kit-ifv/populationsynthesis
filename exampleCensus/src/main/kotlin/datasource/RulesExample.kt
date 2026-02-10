package edu.kit.ifv.populationsynthesis.datasource

import edu.kit.ifv.populationsynthesis.rules.ExhaustiveRuleGenerator
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.contribution.NamedContribution
import edu.kit.ifv.populationsynthesis.rules.covered.ExhaustiveContributionSetSupplier


class AgeRules() : ExhaustiveRuleGenerator<CensusHousehold> {

    val contributionFunctions = AgeSupplier(listOf(0, 1, 2, 3, 4, 5, 6))

    override fun generateRules(): RuleSet<CensusHousehold> {
        return contributionFunctions.generateAllDescriptions().loadTargets(0, 1, 2, 3, 4, 5, 6)
    }
}

class AgeSupplier(private val ageGroupings: List<Int>) : ExhaustiveContributionSetSupplier<CensusHousehold> {
    override fun generateContributions(): List<NamedContribution<CensusHousehold>> {
        val zipWithNext = ageGroupings.zipWithNext { first, second ->
            NamedContribution.numeric<CensusHousehold>("Age Rule [${first}, ${second}]") { household ->
                household.members.count { person ->
                    person.age in first..second
                }
            }
        }
        val last =
            NamedContribution.numeric<CensusHousehold>("Age Rule [${ageGroupings.last()}..Infinity]") { household ->
                household.members.count { person ->
                    person.age > ageGroupings.last()
                }
            }
        return zipWithNext + last
    }
}