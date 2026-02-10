package examples

import edu.kit.ifv.populationsynthesis.rules.*
import edu.kit.ifv.populationsynthesis.rules.measurement.BooleanMeasurementDefinition
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ExampleIPURun {

    private enum class Sex {
        MALE, FEMALE
    }

    private class Person(val age: Int, val sex: Sex) {
        companion object {
            fun random(random: Random = Random(1)): Person {
                return Person((0..100).random(random), Sex.entries.random(random))
            }
        }
    }

    private class HH(val members: List<Person>) {
        companion object {
            fun random(size: Int, random: Random = Random(1)): HH {
                return HH((0 until size).map { Person.random(random) })
            }
        }
    }

    private enum class EqualityOperator(val symbol: String, val operation: (Int, Int) -> Boolean) {
        EQUALS("==", { a, b ->
            a == b
        }),
        GREATER_EQUALS(">=", { a, b ->
            a >= b
        })
    }

    private data class HouseholdSizeMeasurement(
        val targetSize: Int,
        val equalityOp: EqualityOperator,
    ) : BooleanMeasurementDefinition<HH>() {
        override fun evaluation(element: HH): Boolean {
            return equalityOp.operation(element.members.size, targetSize)
        }


        override fun generateDescription(): String = toString()
    }


    private class TestSizeRules private constructor(private val targets: List<IndexedValue<Double>>) :
        ExhaustiveRuleGenerator<HH> {

        constructor(vararg targets: Number) : this(targets.mapIndexed { index, value ->
            IndexedValue(
                index + 1,
                value.toDouble()
            )
        })

        init {
            require(targets.isNotEmpty()) {
                "cannot generate rules from nothing"
            }
        }

        override fun generateRules(): RuleSet<HH> {
            val equalityTargets = targets.dropLast(1)
            val equalityRules = equalityTargets.map { (index, target) ->
                HouseholdSizeMeasurement(index, EqualityOperator.EQUALS).makeRule(target)

            } + HouseholdSizeMeasurement(
                targets.last().index,
                EqualityOperator.GREATER_EQUALS
            ).makeRule(targets.last().value)

            return equalityRules.toRuleSet()

        }
    }

    @Test
    fun exampleRun() {
        val hhRules = TestSizeRules(1, 2, 3, 10)
        val hhRules2 = TestSizeRules(10, 20, 30, 10)
        assertNotEquals(hhRules, hhRules2)
        val firstRuleset = hhRules.generateAllRules()
        val secondRuleset = hhRules2.generateAllRules()
        val thirdRuleset = hhRules2.generateAllRules()
        val households = listOf(
            HH.random(1),
            HH.random(2),
            HH.random(3),
        )
        val output = firstRuleset.delta(households)
        assertEquals(output.entries.first().value, 0.0)
        val outputRelative = firstRuleset.relativeDeltas(households)
        val outputt = firstRuleset.relativeTotals(households)
        firstRuleset.zip(secondRuleset).forEach { (first, second) ->
            assertEquals(first.logic, second.logic)
        }
        assertEquals(firstRuleset.first().logic, thirdRuleset.first().logic)

    }
}