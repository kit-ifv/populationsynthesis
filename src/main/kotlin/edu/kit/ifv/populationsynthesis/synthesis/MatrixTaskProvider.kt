package edu.kit.ifv.populationsynthesis.synthesis

/**
 * Give me the problem matrix for a given area, this is basically all this interface is doing.
 */
fun interface MatrixTaskProvider<AREA, T> {
    fun provideProblemFor(area: AREA): MatrixTask
    operator fun get(area: AREA) = provideProblemFor(area)
}