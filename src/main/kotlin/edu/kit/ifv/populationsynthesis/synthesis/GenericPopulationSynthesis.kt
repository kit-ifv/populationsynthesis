package edu.kit.ifv.populationsynthesis.synthesis


fun interface GenericPopulationSynthesis<AREA, out T> {

    fun synthesize(target: AREA) = synthesize(listOf(target))
    fun synthesize(targetAreas: Collection<AREA>): Map<AREA, List<T>>
}


