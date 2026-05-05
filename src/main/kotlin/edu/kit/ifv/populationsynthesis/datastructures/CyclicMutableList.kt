package edu.kit.ifv.populationsynthesis.datastructures

class CyclicMutableList<T>(
    private val original: MutableList<T>,
) : Collection<T> by original {
    private var currentElementIndex = 0
        set(value) {
            field = modulus(value)

        }


    fun removeCurrent() {
        original.removeAt(currentElementIndex)
        currentElementIndex--
    }

    private fun modulus(value: Int): Int  {
        return if(original.isEmpty()) -1 else Math.floorMod(value, original.size)
    }

    fun remove(element: T): Boolean {
        val index = original.indexOf(element)
        if (index != -1) {
            original.removeAt(index)
            currentElementIndex = modulus(currentElementIndex)
        } else return false
        if(index < currentElementIndex) {
            currentElementIndex--
        }
        return true
    }

    fun next(): T {
        return original[currentElementIndex].also { currentElementIndex++ }
    }
    fun decrementIndex() = currentElementIndex--
}

fun <T> Collection<T>.toCyclicMutableList() = CyclicMutableList(toMutableList())