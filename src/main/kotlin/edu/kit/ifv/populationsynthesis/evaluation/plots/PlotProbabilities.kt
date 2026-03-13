package edu.kit.ifv.populationsynthesis.evaluation.plots


import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.kandy.dsl.continuous
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.letsplot.layers.bars
import org.jetbrains.kotlinx.kandy.letsplot.layers.line
import org.jetbrains.kotlinx.kandy.letsplot.layers.points
import org.jetbrains.kotlinx.kandy.letsplot.scales.Transformation
import kotlin.math.E
import kotlin.math.pow
import kotlin.random.Random

fun plotWrap(numbers: Collection<Pair<Number, Number>>) {
    val plot = ploot(numbers)
    plot.save("plot3.png")
}

fun ploot(numbers: Collection<Pair<Number, Number>>): Plot {
    return ploot(numbers.map { it.first.toDouble() }, numbers.map { it.second.toDouble() })
}

fun ploot(expected: List<Double>, actual: List<Double>) = plot {
    val minVal = 1e-6
    val maxVal = 1.0
    points {
        x(expected) {
            scale = continuous(min = minVal, max = maxVal, transform = Transformation.LOG10)

        }
        y(actual) {
            scale = continuous(min = minVal, max = maxVal, transform = Transformation.LOG10)
        }


    }

}

data class ProbabilityPair(val expected: Double, val actual: Double)

//fun plotProbabilities(numbers: Collection<Pair<Number, Number>>) {
//    return plotProbabilities(numbers.map { it.first.toDouble() }, numbers.map { it.second.toDouble() })
//}
//fun plotProbabilities(expected: List<Double>, actual: List<Double>) {
//
//
//    val data = mapOf(
//        "expected" to expected,
//        "actual" to actual,
//    )
//    val minVal = 1e-6
//    val maxVal = 1.0
//
//    val minPow = floor(log10(minVal)).toInt()
//    val maxPow = ceil(log10(maxVal)).toInt()
//    val breaks = (minPow..maxPow).map { 10.0.pow(it) }
//    val p =
//        ggplot(data) { x = "expected"; y = "actual" } +
//                geomPoint(alpha = 0.6) +
////                geomLine(intercept = 0.0, slope = 1.0) +   // y=x reference in log space too
//                scaleXLog10(limits = minVal to maxVal, breaks = breaks) +
//                scaleYLog10(limits = minVal to maxVal, breaks = breaks) +
//                coordFixed() +
//                ggtitle("Expected vs Actual (log–log)") +
//                xlab("Expected probability") +
//                ylab("Actual probability")
//
//    ggsave(p, "plot2.png")
////    p.show()
//}

fun Collection<Double>.toCumulativeDF(name: String): DataFrame<*> {
    val sorted = sorted()
    val y = sorted.indices.map { (it.toDouble() + 1) / sorted.size }
    val dataFrameOf = dataFrameOf(
        "x" to sorted,
        "y" to y,
    )

    return dataFrameOf.add("name") { name }

}

fun Collection<Double>.plotBin() {
    plot {

    }
}

fun plotEDCF(map: Map<String, Collection<Double>>) {

    val dfs = map.entries.map { (name, v) ->
        v.toCumulativeDF(name)
    }.reduce { acc, df -> acc.concat(df) }
    val minVal = 1e-30
    val maxVal = 1.0
    val plooosot = plot(dfs) {

        line {
            x("x") {
                scale = continuous(max = maxVal, transform = Transformation.LOG10)
            }
            y("y") {
                scale = continuous(max = maxVal, transform = Transformation.LOG10)
            }
            color("name")   // legend

        }


    }
    plooosot.save("plllooooooot.png")
}


data class LogBinConfiguration(
    val numberOfBins: Int = 100,
    val lowestNumber: Double = 1e-6,
    val scaling: Double = 10.0
)

fun plotLogHistograms(map: Map<String, Collection<Double>>) {
    val dfs = map.entries.map { (name, v) ->
        v.toBins(name)
    }.reduce { acc, df -> acc.concat(df) }

    dfs.plot {
        bars {
            x("x")
            y("y") {
                scale = continuous(max = 2000, transform = Transformation.LOG10)
            }
            fillColor("name")     // color by series
            width = 2.0
            alpha = 0.5
        }

    }.save("en.png")

}

fun Collection<Double>.toBins(name: String, configuration: LogBinConfiguration = LogBinConfiguration()): DataFrame<*> {
    val firstRange = 0.1..0.2
    val maxValue = max()
    val groupings = (0..configuration.numberOfBins).reversed().map {
        E.pow(-it.toDouble() / configuration.scaling)
    }
    val group = groupBy {
        groupings.binarySearch(it).toIndex()
    }.toSortedMap().mapValues { it.value.size }
    println(groupings)


    return dataFrameOf(
        "x" to group.keys.toList(),
        "y" to group.values.toList(),

        ).add("name") { name }

}

fun Int.toIndex(): Int {
    return if (this < 0) return -(this + 1) else this
}


fun main() {


    val sampleA = List(1000) { Random.nextDouble() }
    val sampleB = List(1000) { Random.nextDouble() }
    val df = sampleA.toBins("lol")
    val dfb = sampleB.toBins("lul")
    df.concat(dfb).plot {
        bars {
            x("x")
            y("y")
            fillColor("name")     // color by series
            width = 2.0
            alpha = 0.5
        }

    }.save("en.png")
//    val plot = ploot(listOf(0.0, 0.1, 0.2, 0.3, 0.4), listOf(0.4, 0.1, 0.3, 0.2, 0.0))
//    plot.save("plot4.png")


}