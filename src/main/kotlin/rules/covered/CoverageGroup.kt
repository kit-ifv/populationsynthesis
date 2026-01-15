package edu.kit.ifv.populationsynthesis.rules.covered

import edu.kit.ifv.populationsynthesis.rules.Rule

/**
 * A coverage group captures a collection of rules that are associated to a specific observed attribute over
 * the elements so that in total, the rules cover the entire attribute space that can be represented with the
 * target values. For example, an age coverage group is [0-18], [19-Infinity), where every conceivable value
 * falls in a category. The main benefit of grouping the elements like this is that with such a partition each
 * element has to be associated to one of the categories and thus a relative target can be calculated as well as
 * the absolute target for the rules.
 *
 * Why does this interface exist and not just be replaced with the "full coverage group" implementation that
 * derives the total target from the rules themselves. Well consider what happens when the rules have an overlap:
 * Age [0-18] = 10, [10, Infinity) = 20 suddenly an expected total of 30 would not be representative because some
 * elements would be counted twice. In this case you cannot read the total Target from the rules alone, but need
 * an external source to define the total. (Like for example 28).
 */
interface CoverageGroup<T> : List<Rule<T>> {
    val rules: List<Rule<T>>
    val totalTarget: Double
}