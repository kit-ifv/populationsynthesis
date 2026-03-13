package edu.kit.ifv.populationsynthesis.synthesis

interface CompletePopulationSynthesis<AREA, out T>: GenericPopulationSynthesis<AREA, T> {
    fun synthesizeAll(): Map<AREA, List<T>>
}