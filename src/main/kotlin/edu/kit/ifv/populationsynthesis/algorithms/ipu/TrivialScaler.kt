package edu.kit.ifv.populationsynthesis.algorithms.ipu

import edu.kit.ifv.populationsynthesis.algorithms.RuleObserver
import edu.kit.ifv.populationsynthesis.algorithms.ScalableVector

class TrivialScaler : GenericIPU {
    override fun run(
        vectors: Collection<ScalableVector>,
        observers: Collection<RuleObserver>
    ) {
        TODO("just scale up or down the amount of vectors and call it a day")
    }
}