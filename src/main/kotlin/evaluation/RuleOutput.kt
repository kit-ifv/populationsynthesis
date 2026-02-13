package edu.kit.ifv.populationsynthesis.evaluation

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import edu.kit.ifv.populationsynthesis.rules.measurement.LogicIdentifier

@JsonPropertyOrder(
    "area",
    "logicIdentifier",
    "total",
    "actual"
)
data class RuleOutput(
    val area: String,
    val logicIdentifier: LogicIdentifier,
    val total: Double,
    val actual: Double
) {
    override fun toString(): String {
        return "${total - actual} $area ${logicIdentifier.text}"
    }
}