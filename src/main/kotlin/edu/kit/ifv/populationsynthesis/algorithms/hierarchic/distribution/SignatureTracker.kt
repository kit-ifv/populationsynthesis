package edu.kit.ifv.populationsynthesis.algorithms.hierarchic.distribution

import edu.kit.ifv.populationsynthesis.datastructures.Signature
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet
import it.unimi.dsi.fastutil.ints.IntSets

/**
 * Tasked with mapping signatures to ints(read Signature index) and a lot of bijection
 * between attribute indices and the corresponding signature indices.
 */
class SignatureTracker(
    val signatures: List<Signature>,
    amountOfAttributes: Int
) {

    val indices get()= signatures.indices
    private val signatureIndexMapper = signatures.withIndex().associate {
        it.value to SignatureIndex(
            it.index
        )
    }
    val size = signatures.size
    private val inverseIndices = Array(amountOfAttributes) { IntOpenHashSet() }

    /**
     * The largest shift that can occur in a target region by receiving 1 signature.
     * Thats just l1 ........
     */
    val largestDelta = signatures.maxOf { it.manhattanNorm() }

    val largestDifference = 2 * largestDelta

    val largestDeltaPartition = signatures.maxOf { it.manhattanNorm() }

    init {
        require(amountOfAttributes == signatures.maxOf { it.size }) {
            "The highest key is the last attribute that is referenced by the signatures."
        }
        signatures.withIndex().forEach { (i, sig) ->
            sig.nonzeroIndices.forEach { key ->
                inverseIndices[key].add(i)
            }
        }
    }

    fun highestAttributeForIndex(attrIdx: Int) = signatures.maxOf {
        it[attrIdx]
    }

    operator fun get(index: Int): Signature {
        return signatures[index]
    }

    /**
     * Return the indices of all signatures that have a nonzero entry for the requested attribute index.
     */
    fun getSetByAttributeIndex(attrIdx: Int): IntSet {
        return IntSets.unmodifiable(inverseIndices[attrIdx])
    }

    fun getByAttribute(attrIdx: Int): List<Signature> {
        return getSetByAttributeIndex(attrIdx).map { signatures[it] }
    }

    fun findSignatureIndex(signature: Signature): SignatureIndex {
        return signatureIndexMapper[signature] ?: throw NoSuchElementException(
            "There is no signature $signature" +
                    " in $signatureIndexMapper"
        )
    }
}
