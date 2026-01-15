package edu.kit.ifv.populationsynthesis.rules.contribution

interface ContributionOrigin {
    val description: String

    fun <T> ContributionDescriptor<T>.generateDescription() = this.generateNamedFunction(this@ContributionOrigin)
    fun <T> ContributionDescriptor<T>.generateRule(target: Double) = this.generateRule(this@ContributionOrigin, target)
}