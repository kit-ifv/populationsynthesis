package edu.kit.ifv.populationsynthesis.rules.covered

import edu.kit.ifv.populationsynthesis.rules.NamedContribution
import java.util.function.IntFunction

class FullDescriptorGroup<T>(val descriptors: List<NamedContribution<T>>) : List<NamedContribution<T>>  by descriptors {

    fun loadTargets(targets: Collection<Double>): FullCoverageGroup<T> {
        require(targets.size == descriptors.size) { "Mismatch descriptor size ${descriptors.size}, targets ${targets.size}" }
        val rules =  descriptors.zip(targets).map { (desc, t) ->
            desc.withTarget(t)
        }
        return FullCoverageGroup(rules)
    }

    fun loadTargets(vararg targets: Number): FullCoverageGroup<T> = loadTargets(targets.map {
        it.toDouble()
    })
    @Deprecated("Why exactly I cannot tell")
    override fun <T : Any> toArray(generator: IntFunction<Array<out T>>): Array<out T> {
        return super.toArray(generator)
    }

}