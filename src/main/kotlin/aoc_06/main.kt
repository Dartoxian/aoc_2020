package aoc_06

import java.io.File

fun readInput(): List<List<String>> = File(ClassLoader.getSystemResource("aoc_06_input.txt").file)
    .readLines()
    .fold(listOf(mutableListOf()), { acc: List<MutableList<String>>, raw ->
        if (raw.isBlank()) acc + listOf(mutableListOf()) else {acc[acc.lastIndex].add(raw); acc}
    })

fun main() {
    val groupResponses = readInput()
    val sumUnique = groupResponses
        .map { responses -> responses.flatMap { it.toList() }.toSet().size}
        .sum()
    println("The number of unique positive questions across all groups was $sumUnique")

    val sumEveryone = groupResponses
        .map { responses -> responses
            .map { it.toSet() }
            .reduce { a, b -> a.intersect(b)}
            .size
        }
        .sum()
    println("The number of questions everyone in a group answered positively to was $sumEveryone")
}