package edu.kit.ifv.populationsynthesis.rules.contribution

import edu.kit.ifv.populationsynthesis.rules.NamedContribution
import edu.kit.ifv.populationsynthesis.rules.Rule


interface ContributionDescriptor<T> {
    fun generateDescription(): String
    fun generateNamedFunction(origin: ContributionOrigin): NamedContribution<T>
    fun generateRule(origin: ContributionOrigin, target: Double): Rule<T> = generateNamedFunction(origin).withTarget(target)

}


