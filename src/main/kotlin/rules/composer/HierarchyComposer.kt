package edu.kit.ifv.populationsynthesis.rules.composer

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.hierarchy.expandIf
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.contribution.LogicIdentifier
import edu.kit.ifv.populationsynthesis.rules.contribution.NamedContribution
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider
import edu.kit.ifv.populationsynthesis.rules.sumRule
import java.util.BitSet
import kotlin.collections.ArrayDeque
import kotlin.collections.Collection
import kotlin.collections.Map
import kotlin.collections.all
import kotlin.collections.associate
import kotlin.collections.associateBy
import kotlin.collections.associateWith
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.drop
import kotlin.collections.filter
import kotlin.collections.first
import kotlin.collections.flatMap
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty
import kotlin.collections.map
import kotlin.collections.mapNotNull
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.toMutableSet
import kotlin.collections.toSet
import kotlin.collections.withIndex
import kotlin.math.min

// Take a unique rule definition only once, and that is at the lowest level where coverage is achieved.
class HierarchyComposer<AREA, T>(override val hierarchy: HierarchicElement<AREA>) : HierarchyRuleComposer<AREA, T> {



    override fun compose(target: AREA, ruleProvider: RuleProvider<AREA, T>): RuleSet<T> {

        val children = hierarchy.getAllChildren(target) + target
        val leafs = children.filter { hierarchy.isLeaf(it) }
        val associatedRules = children.associateWith { ruleProvider[it] }

        val ruleLookup = associatedRules.mapValues { it.value.associateBy { it.logic } }
        val allLogics = associatedRules.values.flatMap { it.map { it.logic } }.toSet()

        /*
        Make an upward search. Encode the rules that you find as a BitSet As in 011001 means that the second, third
        and sixth rule apply to T. Then we can induce

        A parent covers all rules that
         1) Are the AND conjunction of all coverages of all childs.
         OR
         2) Are given by the rules of the parent.
         */

        val parents = leafs.mapNotNull { hierarchy.getParent(it) }.toMutableSet()
        val queue = ArrayDeque<AREA>(parents)
        val bitsetMap = mutableMapOf<AREA, BitSet>()
        val indexer = allLogics.withIndex().associate { it.value.identifier to it.index }
        leafs.forEach {

            bitsetMap[it] = ruleProvider[it].toBitSet(indexer)
        }
        while(queue.isNotEmpty()) {
            val head = queue.removeFirst()
            val currentChilds = hierarchy.getImmediateChildren(head)
            val bitsets = currentChilds.mapNotNull { bitsetMap[it] }
            val bitsetAnd = bitsets.andAll()
            val myBitset = ruleProvider[head].toBitSet(indexer)
            myBitset.or(bitsetAnd)
            bitsetMap[head] = myBitset
        }

        val output = allLogics.withIndex().associate {  (index, logic) ->
            logic to hierarchy.expandIf(target) { area ->

                /*
                Replace node if: MUST BE PARENT
                    1) Children have coverage: As in all child nodes have been set to handle the rule at index i
                    in the upward search.
                    2) Node does not define rules for target rule index. (Means that coverage fails, but later nodes
                    will define something.

                Keep Node if:
                    1) It is a leaf. Because in that instance no coverage could be formed
                 */
                val isLeaf = hierarchy.isLeaf(area)
                val ch = hierarchy.getImmediateChildren(area)
                val childCoverage = ch.map { bitsetMap[it]?.get(index) ?: false }.all { it }
                val hasRule = ruleLookup[area]?.get(logic) != null
                !isLeaf && (childCoverage || !hasRule)
            }
        }

        val mapValues = output.entries.associate { (k, v) ->
            k.identifier to v.mapNotNull { ruleLookup[it]?.get(k) }.sumRule()
        }
        return RuleSet.create(mapValues)

    }
    fun Collection<Rule<T>>.toBitSet(indexer: Map<LogicIdentifier, Int> ): BitSet {
        val mapping = associateBy { it.logic }
        val bitSet = BitSet()
        mapping.entries.forEach { (c, _) ->

            indexer[c.identifier]?.let { index ->
                bitSet.set(index)
            }


        }
        return bitSet
    }
}

fun BitSet.fill(bitSize: Int) {
    for (i in 0 until min(bitSize, size())) {
        set(i)
    }
}

fun Collection<BitSet>.andAll(): BitSet {
    val first = first().clone() as BitSet
    drop(1).forEach {
        first.and(it)
    }
    return first
}