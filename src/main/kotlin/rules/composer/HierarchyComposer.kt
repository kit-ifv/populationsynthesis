package edu.kit.ifv.populationsynthesis.rules.composer

import edu.kit.ifv.populationsynthesis.hierarchy.HierarchicElement
import edu.kit.ifv.populationsynthesis.hierarchy.expandIf
import edu.kit.ifv.populationsynthesis.rules.RuleLookup
import edu.kit.ifv.populationsynthesis.rules.RuleSet
import edu.kit.ifv.populationsynthesis.rules.composer.bitsets.BitsetMap
import edu.kit.ifv.populationsynthesis.rules.composer.bitsets.MutableBitsetMap
import edu.kit.ifv.populationsynthesis.rules.composer.bitsets.toBitsetMap
import edu.kit.ifv.populationsynthesis.rules.provider.RuleProvider
import edu.kit.ifv.populationsynthesis.rules.sumRule
import edu.kit.ifv.populationsynthesis.rules.toRuleSet

/**
 * Composes a single [RuleSet] for a target area by aggregating rules defined across a hierarchy (DAG).
 *
 * For each rule logic found in the subtree spanning from the target node a conflict free rule will be derived.
 * (A conflict would be when a rule is defined multiple times at different levels in the subtree)
 *
 * For each rule logic the elements of the subtree are scanned for a potential conflict free resolution to
 * aggregate the individual targets to a new compound target.
 * (If multiple areas define a target, like A1=10, A2=20, B1=30 then the induced rule is C=60)
 *
 * The strategy to resolve conflicts is to use the lowest possible subareas to form the rules, so if A1, A2 are
 * defining a rule, and the parent B defines a rule for the same logic, then the sum of A1,A2 is used instead of B
 */
class HierarchyComposer<AREA, T>(override val hierarchy: HierarchicElement<AREA>) : HierarchyRuleComposer<AREA, T> {



    override fun compose(target: AREA, ruleProvider: RuleProvider<AREA, T>): RuleSet<T> {
        val relevantNodes = hierarchy.getAllChildren(target) + target
        val ruleLookup    = RuleLookup.fromProvider(ruleProvider).filter { it in relevantNodes }
        val allLogics     = ruleLookup.logics
        val bitsetMap     = createCoverageBitsetMap(ruleProvider, ruleLookup)

        val ambiguityMap  = bitsetMap.createAmbiguityBitsetMap()
        /*
         * The most complex part of rule composition. For each logic we step through the DAG Rule graph, starting
         * at our target node. Then we check whether a node can (or must) be replaced with its children.
         *
         * A node MUST be replaced when it is not a leaf and has no associated rule for the logic.
         * A node CANNOT be replaced when it is a leaf.
         * A node CAN be replaced when the rule logic is covered by its children, even when the node itself defines
         * a valid rule.
         *
         * This implementation will perform every CAN replacement.
         *
         */
        val logicCoverageMapping = allLogics.withIndex().associate { (index, logic) ->
            logic to hierarchy.expandIf(target) { area ->
                val isLeaf = isLeaf(area)
                val ch = getImmediateChildren(area)
                val childCoverage = bitsetMap.allAreFlagged(ch, index)
                val hasRule = ruleLookup.hasRule(area, logic)
                val hasAmbiguity = ambiguityMap[area, index]
                !isLeaf && (childCoverage || !hasRule || hasAmbiguity)
            }
        }

        /*
         * We now know all the areas that are required to form a compound rule. However, there can still be some areas,
         * leafs in particular, that do not have an associated rule. (When a rule is not covered the expansion strategy
         * resolves down to the leaf level and adds all nodes, even those that do not have a rule for the logic)
         *
         * We need to form a new rule based on the entries, as such we check which rules exist and build a new rule
         * with the summation strategy.
         */
        val fusedRules = logicCoverageMapping.entries.associate { (k, v) ->
            k to v.mapNotNull { ruleLookup[it, k]?.rule }.sumRule()
        }
        return fusedRules.toRuleSet()

    }



    /*
     * There is one difficult scenario: When the hierarchy is a DAG, a child node defines a rule, the child has
     * multiple parents, the parents have multiple children and the parents also define the rule but the other
     * children do not define a rule. In that instance the parent nodes MUST be replaced.
     *
     * Essentially this scenario translates to an ambiguity definition: If a (child) node has any different
     * ancestors that also define a rule, and the ancestors are unrelated, then both of the ancestors MUST be
     * replaced for this particular rule.
     *
     * This can be solved by iterating from each leaf. Once a node has more than 1 parent, then each of these parents,
     * as well as all nodes later in the graph can be flagged as MUST replacement for that particular rule.
     */
    private fun BitsetMap<AREA>.createAmbiguityBitsetMap(): BitsetMap<AREA> {
        val bitsetMap = MutableBitsetMap<AREA>()

        val nodesWithMultipleParents = hierarchy.getAllVertices().filter { hierarchy.getParents(it).size > 1 }
        nodesWithMultipleParents.forEach { ambiguousNode ->
            val ancestors = hierarchy.getAllAncestors(ambiguousNode)
            val currentBitset = this[ambiguousNode]
            ancestors.forEach { ancestor ->
                val ancestorBitset = bitsetMap.getOrPut(ancestor)
                ancestorBitset.or(currentBitset)
            }

        }
        return bitsetMap
    }

    private fun createCoverageBitsetMap(
        ruleProvider: RuleProvider<AREA, T>,
        ruleLookup: RuleLookup<AREA, T>
    ): BitsetMap<AREA> {

        val leafs = ruleLookup.areas.filter { hierarchy.isLeaf(it) }


        val queue = ArrayDeque(leafs.mapNotNull { hierarchy.getParents(it) }.flatten())
        /* Create a bitset map where each node in the provider is associated with the bitset where defined rules
        *  for the area are set to 1. For example if an area has a definition for the following (indexed) Rules:
        *  R_0, R_2, R_3 then the bitset would look like this [1011].
        */
        val bitsetMap = ruleLookup.toBitsetMap(ruleProvider)


        /*
        The bitset map is intended to indicate whether a rule (referenced by index in the bitset) is covered by a rule
        There are exactly 2 scenarios in which a node covers a rule
        1) A rule already exists for the node (trivial coverage)
        2) All child nodes are covered.

        The bitset map is already initialized 1) by construction. Once a node is handled we can update the bitset
        with the OR conjunction of Itself ||  Π(childs)
         */

        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            val children = hierarchy.getImmediateChildren(node)
            val childBitsets = bitsetMap.andConjunction(children)
            val myBitset = bitsetMap[node]
            myBitset.or(childBitsets) // Works via side effects onto the receiver, so we don't need to update the map
        }
        return bitsetMap
    }


}

