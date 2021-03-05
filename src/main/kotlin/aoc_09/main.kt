package aoc_09

import java.io.File
import java.util.*

fun xmasValidator(prefixLength: Long): (Long) -> Boolean {
    var buffer = LinkedList<Long>()
    return { value: Long ->
        if (buffer.size < prefixLength) {
            buffer.addFirst(value)
            true
        } else {
            val currentValues = buffer.toSet()
            if (buffer.any { bufferValue -> value - bufferValue in currentValues && bufferValue * 2 != value }) {
                buffer.removeLast()
                buffer.addFirst(value)
                true
            } else {
                false
            }
        }
    }
}

fun main() {
    val firstInvalidNumber = File(ClassLoader.getSystemResource("aoc_09_input.txt").file)
        .readLines()
        .asSequence()
        .map(String::toLong)
        .filterNot(xmasValidator(25)).first()

    println("First invalid number was $firstInvalidNumber")

    val allNumbers = File(ClassLoader.getSystemResource("aoc_09_input.txt").file)
        .readLines()
        .map(String::toLong)

    val (s, e) = allNumbers.indices
        .flatMap { i -> ((i + 1)..allNumbers.lastIndex).map { j -> i to j } }
        .find { (i, j) -> allNumbers.slice(i..j).sum() == firstInvalidNumber }
        ?: throw IllegalArgumentException("Not possible to find encryption weakness")

    val min = allNumbers.slice(s..e).minOrNull() ?: throw IllegalArgumentException("Not possible to find encryption weakness")
    val max = allNumbers.slice(s..e).maxOrNull() ?: throw IllegalArgumentException("Not possible to find encryption weakness")

    println("The encryption weakness is ${min + max}")
}