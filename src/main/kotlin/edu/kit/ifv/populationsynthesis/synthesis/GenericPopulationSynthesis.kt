package edu.kit.ifv.populationsynthesis.synthesis


fun interface GenericPopulationSynthesis<AREA, out H> {

    fun synthesize(target: AREA) = synthesize(listOf(target))
    fun synthesize(targetAreas: List<AREA>): Map<AREA, List<H>>
}


