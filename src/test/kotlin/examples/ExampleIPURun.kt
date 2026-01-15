package examples

import edu.kit.ifv.populationsynthesis.rules.ExhaustiveRuleGenerator
import edu.kit.ifv.populationsynthesis.rules.NamedContribution
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.contribution.BooleanContributionDefinition
import edu.kit.ifv.populationsynthesis.rules.contribution.ContributionDescriptor
import edu.kit.ifv.populationsynthesis.rules.contribution.ContributionOrigin
import edu.kit.ifv.populationsynthesis.rules.covered.ExhaustiveContributionGenerator
import edu.kit.ifv.populationsynthesis.rules.toScalableVector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ExampleIPURun {

    private enum class Sex {
        MALE, FEMALE
    }

    private class Person(val age: Int, val sex: Sex)

    private class HH(val members: MutableList<Person>)
    private enum class EqualityOperator(val symbol: String, val operation: (Int, Int) -> Boolean) {
        EQUALS("==", { a, b ->
            a == b
        }),
        GREATER_EQUALS(">=", { a, b ->
            a >= b
        })
    }

    private data class Lol(
        val targetSize: Int,
        val equalityOp: EqualityOperator,
    ) : BooleanContributionDefinition<HH>() {
        override fun evaluation(element: HH): Boolean {
            return equalityOp.operation(element.members.size, targetSize)
        }



        override fun generateDescription(): String = toString()
    }

    private class AARules(private val sex: Sex, private val targets: List<Int>) : ExhaustiveContributionGenerator<HH> {
        init {
            require(targets.isNotEmpty()) {
                "This wont work"
            }
        }
        constructor(sex: Sex, vararg targets: Number) : this(
            sex,
            targets.map { it.toInt() })
        override fun generateDescriptions(): List<NamedContribution<HH>> {
            val firstRule = NamedContribution.numeric<HH>("It ${0..<targets.first()} Sex=$sex", this) { hh ->
                hh.members.count { it.sex == sex && it.age < targets.first() }
            }
            return listOf(firstRule) + targets.zipWithNext().map { (a, b) ->

                NamedContribution.numeric("It ${a..<b} Sex=$sex", this) { hh ->
                    hh.members.count { it.sex == sex && it.age in a..<b }
                }
            } + NamedContribution.numeric("It b>=+ Sex=$sex", this) { hh ->
                hh.members.count { it.sex == sex && it.age >= targets.last() }
            }

        }

        override val description: String
            get() = TODO("Not yet implemented")
    }

    private class TestAgeRules(private val sex: Sex, private val targets: List<IndexedValue<Double>>) :
        ExhaustiveRuleGenerator<HH> {
        constructor(sex: Sex, vararg targets: Number) : this(
            sex,
            targets.mapIndexed { index, value -> IndexedValue(index, value.toDouble()) })


        override fun generateRules(): List<Rule<HH>> {
            return targets.map { (index, value) ->

                NamedContribution.numeric<HH>("It $index", this) { hh ->
                    hh.members.count { it.sex == sex && it.age in 0..index }
                }.withTarget(value)
            }
        }

        override val description: String
            get() = TODO("Not yet implemented")
    }

    private class TestSizeRules private constructor(private val targets: List<IndexedValue<Double>>) :
        ExhaustiveRuleGenerator<HH> {

        constructor(vararg targets: Number) : this(targets.mapIndexed { index, value ->
            IndexedValue(
                index,
                value.toDouble()
            )
        })

        override val description: String = "Standard household size Spawner"

        init {
            require(targets.isNotEmpty()) {
                "cannot generate rules from nothing"
            }
        }

        override fun generateRules(): List<Rule<HH>> {
            val equalityTargets = targets.dropLast(1)
            val equalityRules = equalityTargets.map { (index, target) ->
                Lol(index, EqualityOperator.EQUALS).makeRule(target)

            } + Lol(targets.last().index, EqualityOperator.GREATER_EQUALS).makeRule(targets.last().value)

            return equalityRules

        }
    }

    @Test
    fun exampleRun() {
        val hhRules = TestSizeRules(10, 20, 30, 10)
        val hhRules2 = TestSizeRules(10, 20, 30, 10)
        assertNotEquals(hhRules, hhRules2)
        val firstRuleset = hhRules.generateAllRules()
        val secondRuleset = hhRules2.generateAllRules()
        val thirdRuleset = hhRules2.generateAllRules()

        firstRuleset.zip(secondRuleset).forEach { (first, second) ->
            assertEquals(first.logic, second.logic)
        }
        assertEquals(firstRuleset.first().logic, thirdRuleset.first().logic)

    }
}