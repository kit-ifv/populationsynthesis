package edu.kit.ifv.populationsynthesis.datastructures

import org.ojalgo.matrix.store.R064Store
import kotlin.test.Test


class OjStructureTest  {
    @Test

    fun testMatrixStructure() {

        val matrix = R064Store.FACTORY.make(3, 3)
        matrix[0, 0] = 1
        val col = matrix.column(1)
        println(matrix[0, 0])
    }
}