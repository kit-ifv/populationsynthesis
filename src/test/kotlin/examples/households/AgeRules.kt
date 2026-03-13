package examples.households

import edu.kit.ifv.populationsynthesis.rules.covered.ExhaustiveMeasurementSetSupplier
import edu.kit.ifv.populationsynthesis.rules.covered.FullDescriptorGroup
import edu.kit.ifv.populationsynthesis.rules.measurement.NamedMeasurement

class AgeRules(private val ageThresholds: List<Int>) : ExhaustiveMeasurementSetSupplier<TestPerson> {

    constructor(vararg ageThresholds: Int) : this(ageThresholds.toList())

    override fun generateAllDescriptions(): FullDescriptorGroup<TestPerson> {
        val contributions = listOf(AgeMeasurement(0..ageThresholds.first())) +
                ageThresholds.zipWithNext { a, b ->
                    AgeMeasurement(a..b)
                } + AgeMeasurement(ageThresholds.last()..Int.MAX_VALUE)
        return FullDescriptorGroup(contributions.map { it.createNamedMeasurement() })
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AgeRules) return false
        return ageThresholds == other.ageThresholds
    }

    override fun hashCode(): Int {
        return ageThresholds.hashCode()
    }

    override fun generateMeasurements(): List<NamedMeasurement<TestPerson>> {
        TODO("Not yet implemented")
    }
}

class AgeRuleBuilder(
    private val thresholds: MutableList<Int>
) {
    fun build() {

    }
}