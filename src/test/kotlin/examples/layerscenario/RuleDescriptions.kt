package examples.layerscenario

import edu.kit.ifv.populationsynthesis.rules.ExhaustiveRuleGenerator
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition
import edu.kit.ifv.populationsynthesis.rules.measurement.NamedMeasurement
import edu.kit.ifv.populationsynthesis.rules.measurement.NumericMeasurementDefinition
import edu.kit.ifv.populationsynthesis.rules.covered.ExhaustiveMeasurementSetSupplier
import edu.kit.ifv.populationsynthesis.rules.toRuleSet


data class KonduriPersonTypeDescription(val code: Int) : NumericMeasurementDefinition<KonduriHousehold>() {
    override fun generateDescription(): String {
        return toString()
    }

    override fun evaluation(element: KonduriHousehold): Number {
        return element.members.count { it.personType.code == this.code }
    }
}

data class KonduriHouseholdTypeDescription(val code: Int) : BooleanMeasurementDefinition<KonduriHousehold>() {
    override fun generateDescription(): String = toString()
    override fun evaluation(element: KonduriHousehold): Boolean {
        return element.householdType.code == this.code
    }
}

data class KonduriRegionTypeDescription(val code: Int) : BooleanMeasurementDefinition<KonduriHousehold>() {

    override fun generateDescription(): String = toString()
    override fun evaluation(element: KonduriHousehold): Boolean = element.regionHouseholdType.code == this.code
}

object PTypeSet : ExhaustiveMeasurementSetSupplier<KonduriHousehold> {
    override fun generateMeasurements(): List<NamedMeasurement<KonduriHousehold>> {
        return listOf(
            KonduriPersonTypeDescription(1),
            KonduriPersonTypeDescription(2),
            KonduriPersonTypeDescription(3)
        ).map { it.createNamedMeasurement() }
    }
}

class PTypeGenerator(val one: Int, val second: Int, val third: Int) : ExhaustiveRuleGenerator<KonduriHousehold> {
    private val measurementSupplier = PTypeSet
    override fun generateRules(): RuleSet<KonduriHousehold> {
        return measurementSupplier.generateMeasurements().zip(listOf(one, second, third)).map { (desc, target) ->
            desc.withTarget(target.toDouble())
        }.toRuleSet()
    }

}

object HTypeSet : ExhaustiveMeasurementSetSupplier<KonduriHousehold> {
    override fun generateMeasurements(): List<NamedMeasurement<KonduriHousehold>> {
        return listOf(
            KonduriHouseholdTypeDescription(1),
            KonduriHouseholdTypeDescription(2)
        ).map { it.createNamedMeasurement() }
    }
}

class HTypeGenerator(val one: Int, val two: Int) : ExhaustiveRuleGenerator<KonduriHousehold> {
    override fun generateRules(): RuleSet<KonduriHousehold> {
        return HTypeSet.generateMeasurements().zip(listOf(one, two)).map { (desc, target) ->
            desc.withTarget(target.toDouble())
        }.toRuleSet()
    }
}

object RTypeSet : ExhaustiveMeasurementSetSupplier<KonduriHousehold> {
    override fun generateMeasurements(): List<NamedMeasurement<KonduriHousehold>> {
        return listOf(
            KonduriRegionTypeDescription(1),
            KonduriRegionTypeDescription(2),
            KonduriRegionTypeDescription(3)
        ).map { it.createNamedMeasurement() }
    }
}

class RTypeGenerator(val one: Int, val two: Int, val three: Int) : ExhaustiveRuleGenerator<KonduriHousehold> {
    override fun generateRules(): RuleSet<KonduriHousehold> {
        return RTypeSet.generateMeasurements().zip(listOf(one, two, three)).map { (desc, target) ->
            desc.withTarget(target.toDouble())
        }.toRuleSet()
    }
}