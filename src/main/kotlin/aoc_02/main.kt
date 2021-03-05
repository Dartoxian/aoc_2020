package aoc_02

import java.io.File

data class Policy(val occurrences: IntRange, val requirement: Char) {
    fun test(value: String): Boolean = value.filter { it == requirement }.count() in occurrences

    fun testSecondPolicy(value: String): Boolean =
        (value[occurrences.start - 1] == requirement) xor (value[occurrences.endInclusive - 1] == requirement)
}

fun parseLine(s: String): Pair<Policy, String> {
    try {
        val (policyRaw, test) = s.split(": ")
        val m = """(\d+)-(\d+) (\w)""".toRegex().matchEntire(policyRaw)
            ?: throw IllegalArgumentException("Policy $s cannot be parsed")
        return Policy(m.groups[1]!!.value.toInt()..m.groups[2]!!.value.toInt(), m.groups[3]!!.value[0]) to test
    } catch (e: IndexOutOfBoundsException) {
        throw IllegalArgumentException("Could not parse $s", e)
    }
}

fun readInput(): List<Pair<Policy, String>> = File(ClassLoader.getSystemResource("aoc_02_input.txt").file)
    .readLines()
    .map(::parseLine)

fun main() {
    val inputs = readInput()

    val validPasswords = inputs.filter { (policy, password) -> policy.test(password) }
    println("There are ${validPasswords.size} valid passwords according to the first policy")

    val validPasswordsSecond = inputs.filter { (policy, password) -> policy.testSecondPolicy(password) }
    println("There are ${validPasswordsSecond.size} valid passwords according to the second policy")
}