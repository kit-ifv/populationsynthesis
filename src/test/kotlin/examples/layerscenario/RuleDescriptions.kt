package examples.layerscenario

import edu.kit.ifv.populationsynthesis.rules.ExhaustiveRuleGenerator
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleGenerator
import edu.kit.ifv.populationsynthesis.rules.contribution.BooleanContributionDefinition
import edu.kit.ifv.populationsynthesis.rules.contribution.NamedContribution
import edu.kit.ifv.populationsynthesis.rules.contribution.NumericContributionDefinition
import edu.kit.ifv.populationsynthesis.rules.covered.ExhaustiveContributionSetSupplier


data class KonduriPersonTypeDescription(val code: Int): NumericContributionDefinition<KonduriHousehold>() {
    override fun generateDescription(): String {
        return toString()
    }

    override fun evaluation(element: KonduriHousehold): Number {
        return element.members.count { it.personType.code == this.code }
    }
}

data class KonduriHouseholdTypeDescription(val code: Int): BooleanContributionDefinition<KonduriHousehold>() {
    override fun generateDescription(): String  = toString()
    override fun evaluation(element: KonduriHousehold): Boolean {
        return element.householdType.code == this.code
    }
}

data class KonduriRegionTypeDescription(val code: Int): BooleanContributionDefinition<KonduriHousehold>() {

    override fun generateDescription(): String  = toString()
    override fun evaluation(element: KonduriHousehold): Boolean = element.regionHouseholdType.code == this.code
}

object PTypeSet: ExhaustiveContributionSetSupplier<KonduriHousehold> {
    override fun generateContributions(): List<NamedContribution<KonduriHousehold>> {
        return listOf(KonduriPersonTypeDescription(1), KonduriPersonTypeDescription(2), KonduriPersonTypeDescription(3)).map { it.createNamedContribution() }
    }
}
class PTypeGenerator(val one: Int, val second: Int, val third: Int): ExhaustiveRuleGenerator<KonduriHousehold> {
    private val contributionSupplier = PTypeSet
    override fun generateRules(): List<Rule<KonduriHousehold>> {
        return contributionSupplier.generateContributions().zip(listOf(one, second, third)).map { (desc, target) ->
            desc.withTarget(target.toDouble())
        }
    }

}

object HTypeSet: ExhaustiveContributionSetSupplier<KonduriHousehold> {
    override fun generateContributions(): List<NamedContribution<KonduriHousehold>> {
        return listOf(KonduriHouseholdTypeDescription(1), KonduriHouseholdTypeDescription(2)).map { it.createNamedContribution() }
    }
}

class HTypeGenerator(val one: Int, val two: Int): ExhaustiveRuleGenerator<KonduriHousehold> {
    override fun generateRules(): List<Rule<KonduriHousehold>> {
        return HTypeSet.generateContributions().zip(listOf(one, two)).map {(desc, target) ->
            desc.withTarget(target.toDouble())
        }
    }
}

object RTypeSet: ExhaustiveContributionSetSupplier<KonduriHousehold> {
    override fun generateContributions(): List<NamedContribution<KonduriHousehold>> {
        return listOf(KonduriRegionTypeDescription(1), KonduriRegionTypeDescription(2), KonduriRegionTypeDescription(3)).map { it.createNamedContribution() }
    }
}

class RTypeGenerator(val one: Int, val two: Int, val three: Int): ExhaustiveRuleGenerator<KonduriHousehold> {
    override fun generateRules(): List<Rule<KonduriHousehold>> {
        return RTypeSet.generateContributions().zip(listOf(one, two, three)).map {(desc, target) ->
            desc.withTarget(target.toDouble())
        }
    }
}