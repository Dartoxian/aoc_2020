package aoc_10

import java.io.File

val socketRating = 0
val allAdapterRatings = File(ClassLoader.getSystemResource("aoc_10_input.txt").file)
    .readLines()
    .map(String::toInt)
val laptopRating = (allAdapterRatings.maxOrNull() ?: throw IllegalArgumentException("Cannot find max value in adapters")) + 3

val allRatings = listOf(socketRating) + allAdapterRatings + listOf(laptopRating)

fun main() {
    val differenceToOccurrences = allRatings.sorted()
        .zipWithNext()
        .map { (x, y) -> y - x}
        .groupBy { it }
        .mapValues { it.value.size }

    println("There are ${differenceToOccurrences[1]} 1-jolt differences, and ${differenceToOccurrences[3]} 3-jolt differences")
    println("Result is ${differenceToOccurrences[1]!! * differenceToOccurrences[3]!!}")
    println()

    val routesTo = mutableMapOf<Int, Long>()
    allRatings.sorted().forEach { adapter ->
        if (adapter == 0) {
            routesTo[adapter] = 1
        } else {
            routesTo[adapter] =
                (routesTo[adapter - 1] ?: 0) + (routesTo[adapter - 2] ?: 0) + (routesTo[adapter - 3] ?: 0)
        }
    }
    println("With the adapters we have there are ${routesTo[laptopRating]} routes to $laptopRating jolts")
}