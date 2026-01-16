package edu.kit.ifv.populationsynthesis.rules.covered

import edu.kit.ifv.populationsynthesis.rules.contribution.NamedContribution

fun interface ContributionSetSupplier<T> {
    fun generateContributions(): List<NamedContribution<T>>
}