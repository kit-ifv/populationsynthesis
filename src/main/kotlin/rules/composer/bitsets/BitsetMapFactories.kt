package edu.kit.ifv.populationsynthesis.rules.composer.bitsets

import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleLookup
import edu.kit.ifv.populationsynthesis.rules.contribution.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider
import java.util.BitSet
import kotlin.collections.component1
import kotlin.collections.component2

fun <AREA> RuleLookup<AREA, *>.toBitsetMap(
    ruleProvider: RuleProvider<AREA, *>
): MutableBitsetMap<AREA> {
    val indexer = logics.withIndex().associate { it.value to it.index }
    return MutableBitsetMap<AREA>().apply {
        areas.forEach {
            this[it] = ruleProvider[it].toBitSet(indexer)
        }
    }
}

fun Collection<Rule<*>>.toBitSet(indexer: Map<LogicIdentifier, Int>): BitSet {
    val mapping = associateBy { it.logic }
    val bitSet = BitSet()
    mapping.entries.forEach { (c, _) ->
        indexer[c.identifier]?.let { index ->
            bitSet.set(index)
        }
    }
    return bitSet
}