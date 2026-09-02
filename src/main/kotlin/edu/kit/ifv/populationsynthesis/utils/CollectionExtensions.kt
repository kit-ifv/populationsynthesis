package edu.kit.ifv.populationsynthesis.utils

import kotlin.random.Random

/**
 * Select an exact amount from a list, if the list is not sufficiently long enough, it will be artificially filled by
 * repeating the elements. behaves as [cycleToSize] with the difference that at the end the result list will
 * be shuffled as well.
 */
fun <T> Collection<T>.cycleToSizeAndShuffle(amount: Int, random: Random = Random(1)): List<T> {
    val repeatedList = cycleToSize(amount)
    return repeatedList.shuffled(random)
}

/**
 * Return a list of size [amount] by repeatedly iterating over the collection in a cyclical manner, so if
 * the requested amount exceeds the collection size, the iteration process loops back to the beginning of
 * the collection.
 */
fun <T> Collection<T>.cycleToSize(amount: Int): List<T> {
    if (amount == 0) return emptyList()
    require(isNotEmpty()) {
        "Cannot select an exact amount of elements from an empty collection"
    }
    val inputList = toList()
    return List(amount) { inputList[it % size] }
}

fun <T> Collection<T>.pickWithReplacement(
    amount: Int,
    random: Random = Random(1)
): List<T> {
    val inputList = toList()
    return List(amount) { inputList[random.nextInt(inputList.size)] }
}
