package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

/**
 * The distributor is tasked with taking an initial solution of signature amounts, a root area and distributing
 * the signatures to different areas, converting them back to the type T of the original elements.
 */
fun interface Distributor<AREA, out T> {

    fun distribute(

        initialSolution: List<SignatureAmount>,
        rootArea: AREA,
    ): Map<AREA, List<T>>
}