package aoc_16

import java.io.File

data class Rule(val fieldName: String, val validRanges: List<LongRange>) {
    fun isValueValid(value: Long) = validRanges.any { value in it }
}

fun String.toRule(): Rule {
    fun String.toLongRange(): LongRange {
        val (start, end) = split("-")
        return start.toLong()..end.toLong()
    }

    val (fieldName, ranges) = split(": ")
    return Rule(fieldName, ranges.split(" or ").map { it.toLongRange() })
}

data class Ticket(val fields: List<Long>)

fun String.toTicket(): Ticket = Ticket(split(",").map { it.toLong() })

fun readInput(): Triple<List<Rule>, Ticket, List<Ticket>> {
    val lines = File(ClassLoader.getSystemResource("aoc_16_input.txt").file)
        .readLines()

    var i = 0
    val rules = mutableListOf<Rule>()
    while (lines[i].isNotEmpty()) {
        rules.add(lines[i].toRule())
        i++
    }
    i++
    if (lines[i] != "your ticket:") {
        throw IllegalArgumentException("Expected your ticket line")
    }
    i++
    val myTicket = lines[i].toTicket()
    i++
    i++
    if (lines[i] != "nearby tickets:") {
        throw IllegalArgumentException("Expected nearby tickets line")
    }
    i++
    val nearbyTickets = mutableListOf<Ticket>()
    while (i < lines.size) {
        nearbyTickets.add(lines[i].toTicket())
        i++
    }
    return Triple(rules, myTicket, nearbyTickets)
}

fun main() {
    val (rules, myTicket, nearbyTickets) = readInput()

    val invalidNearbyFields = nearbyTickets
        .flatMap { it.fields }
        .filter { fieldValue -> rules.none { rule -> rule.isValueValid(fieldValue) } }
    println("Ticket scan error rate is ${invalidNearbyFields.sum()}")

    val validNearbyTickets = nearbyTickets
        .filter { ticket -> ticket.fields.all { field -> rules.any { rule -> rule.isValueValid(field) } } }

    val fieldIdToPossibleName = myTicket.fields.indices.associateWith { fieldId ->
        rules
            .filter { rule -> validNearbyTickets.all { ticket -> rule.isValueValid(ticket.fields[fieldId]) } }
            .map { it.fieldName }
            .toMutableSet()
    }
    var fieldIdToCertainName = fieldIdToPossibleName.values.filter { names -> names.size == 1 }.flatten().toSet()
    while (fieldIdToPossibleName.any { (_, names) -> names.size > 1 }) {
        fieldIdToPossibleName.filterValues { names -> names.size > 1 }.forEach { (_, names) ->
            names.removeAll(fieldIdToCertainName)
        }
        fieldIdToCertainName = fieldIdToPossibleName.values.filter { names -> names.size == 1 }.flatten().toSet()
    }

    println(fieldIdToPossibleName)

    val challengeResult = fieldIdToPossibleName
        .mapValues { (_, names) -> names.single() }
        .toList()
        .sortedBy { (fieldId, _) -> fieldId}
        .map { (_, name) -> name }
        .zip(myTicket.fields)
        .filter { (name, _) -> name.startsWith("departure")}
        .map { (_, fieldValue) -> fieldValue }
        .reduce { a, b -> a * b }
    println("The result of the second part of the challenge is $challengeResult")
}
