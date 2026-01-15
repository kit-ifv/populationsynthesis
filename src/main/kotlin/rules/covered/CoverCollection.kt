package edu.kit.ifv.populationsynthesis.rules.covered

import edu.kit.ifv.populationsynthesis.rules.Rule
import java.util.function.IntFunction

class CoverCollection<T> private constructor(
    val rules: List<Rule<T>>,
    val coverageGroups: List<CoverageGroup<T>>
) : List<Rule<T>> by rules {
    constructor(coverageGroups: List<CoverageGroup<T>>) : this(coverageGroups.flatMap { it.rules }, coverageGroups)

}