package edu.kit.ifv.populationsynthesis.algorithms.ipu

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver

data class DirectRunReport(
    val observers: List<RuleObserver>,
    val amountOfZeroVectors: Int
)