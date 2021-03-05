package aoc_01

import java.io.File

fun readInput(): List<Int> = File(ClassLoader.getSystemResource("aoc_01_input.txt").file)
    .readLines()
    .map(Integer::parseInt)

fun solvePair(inputs: List<Int>) {
    val (x, y) = inputs.flatMapIndexed { index, input ->
        ((index + 1) until inputs.size).map { pairIndex -> input to inputs[pairIndex] }
    }.find { (x, y) -> x + y == 2020 } ?: throw IllegalArgumentException("No solution exists for input $inputs")
    println("Solution for a pair is $x * $y = ${x * y}")
}

fun solveTriple(inputs: List<Int>) {
    val remainders = inputs.map { x -> 2020 - x }.toHashSet()
    if (remainders.size != inputs.size) {
        throw IllegalArgumentException("Assumption that inputs are distinct is wrong")
    }

    val (x, y) = inputs.flatMapIndexed { index, input ->
        ((index + 1) until inputs.size).map { pairIndex -> input to inputs[pairIndex] }
    }.find { (x, y) -> (x + y) in remainders } ?: throw IllegalArgumentException("No solution exists for the triple")
    val z = 2020 - x - y
    println("Solution for a triple is $x * $y * $z = ${x * y * z}")
}

fun main() {
    val inputs = readInput()
    solvePair(inputs)
    solveTriple(inputs)
}