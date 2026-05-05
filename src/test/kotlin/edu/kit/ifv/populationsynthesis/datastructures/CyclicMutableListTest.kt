package edu.kit.ifv.populationsynthesis.datastructures

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class CyclicMutableListTest {
    @Test
    fun properRemoval() {
        val cyclic = (0..10).toList().toCyclicMutableList()

        cyclic.next()
        val currentElement = cyclic.next()
        assertEquals(1, currentElement)

        cyclic.remove(9)
        assertEquals(2, cyclic.next())
        cyclic.remove(0)
        assertEquals(3, cyclic.next())
        cyclic.remove(4)
        assertEquals(5, cyclic.next())
    }

    @Test
    fun firstElementRemoval() {
        val cyclic = (0..10).toList().toCyclicMutableList()
        cyclic.remove(0)
        assertEquals(1, cyclic.next())
    }

    @Test
    fun lastElementRemoval() {
        val cyclic = (0..10).toList().toCyclicMutableList()
        repeat(10) {
            cyclic.next()
        }
        cyclic.remove(10)
        assertEquals(0, cyclic.next())
    }
}