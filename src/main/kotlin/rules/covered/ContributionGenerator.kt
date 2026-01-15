package edu.kit.ifv.populationsynthesis.rules.covered

import edu.kit.ifv.populationsynthesis.rules.NamedContribution
import edu.kit.ifv.populationsynthesis.rules.contribution.ContributionOrigin

interface ContributionGenerator<T>: ContributionOrigin {
    fun generateDescriptions(): List<NamedContribution<T>>
}