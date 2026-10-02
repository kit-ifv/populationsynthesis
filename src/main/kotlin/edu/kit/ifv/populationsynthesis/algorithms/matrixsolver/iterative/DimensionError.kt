package edu.kit.ifv.populationsynthesis.algorithms.matrixsolver.iterative

data class DimensionError(
    val target: Double,
    val actual: Double,
) {
    val residual: Double
        get() = target - actual

    val absoluteError: Double
        get() = kotlin.math.abs(residual)

    val relativeError: Double?
        get() = if (target == 0.0) null else residual / target

    val percentError: Double?
        get() = relativeError?.times(100.0)
}
