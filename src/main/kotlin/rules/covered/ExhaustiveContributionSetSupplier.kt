package edu.kit.ifv.populationsynthesis.rules.covered

fun interface ExhaustiveContributionSetSupplier<T> : ContributionSetSupplier<T> {
    fun generateAllDescriptions(): FullDescriptorGroup<T> = FullDescriptorGroup(generateContributions())
}

