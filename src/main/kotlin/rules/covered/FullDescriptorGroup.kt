package edu.kit.ifv.populationsynthesis.rules.covered

import edu.kit.ifv.populationsynthesis.rules.measurement.NamedMeasurement
import edu.kit.ifv.populationsynthesis.rules.toRuleSet

class FullDescriptorGroup<T>(val descriptors: List<NamedMeasurement<T>>) : List<NamedMeasurement<T>> by descriptors {

    fun loadTargets(targets: Collection<Double>): FullCoverageGroup<T> {
        require(targets.size == descriptors.size) { "Mismatch descriptor size ${descriptors.size}, targets ${targets.size}" }
        val rules = descriptors.zip(targets).map { (desc, t) ->
            desc.withTarget(t)
        }
        return FullCoverageGroup(rules.toRuleSet())
    }

    fun loadTargets(vararg targets: Number): FullCoverageGroup<T> = loadTargets(targets.map {
        it.toDouble()
    })

}