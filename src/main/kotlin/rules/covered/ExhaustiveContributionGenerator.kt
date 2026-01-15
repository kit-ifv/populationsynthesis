package edu.kit.ifv.populationsynthesis.rules.covered

interface ExhaustiveContributionGenerator<T>: ContributionGenerator<T> {
    fun generateAllDescriptions(): FullDescriptorGroup<T> = FullDescriptorGroup(generateDescriptions())
}

