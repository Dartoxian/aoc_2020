package aoc_19

import java.io.File

class Rule(val raw: String) {
    lateinit var matches: Set<String>

    fun setupMatches(rules: Map<String, Rule>) {
        val matches = mutableSetOf<String>()
        for (optionRaw in raw.split(" | ")) {
            if (optionRaw.startsWith("\"")) {
                matches.add(optionRaw.subSequence(1 until optionRaw.indexOf('"', startIndex = 1)).toString())
            } else {
                var candidates = setOf("")
                for (ruleId in optionRaw.split(" ")) {
                    val r = rules[ruleId] ?: throw IllegalArgumentException("Cannot find rule $ruleId")
                    if (!r::matches.isInitialized) {
                        rules[ruleId]?.setupMatches(rules)
                    }
                    candidates = candidates.flatMap { candidate -> r.matches.map { candidate + it }}.toSet()
                    if (candidates.any{it.length > 100}) {
                        break
                    }
                }
                matches.addAll(candidates)
            }
        }
        this.matches = matches
    }

    override fun toString(): String {
        return "Rule(raw='$raw')"
    }
}

fun readInput(): Pair<MutableMap<String, Rule>, List<String>> {
    val rawLines = File(ClassLoader.getSystemResource("aoc_19_input.txt").file)
        .readLines()
    val rules = mutableMapOf<String, Rule>()
    var i = 0
    while (rawLines[i] != "") {
        val (id, rawRule) = rawLines[i].split(": ")
        rules[id] = Rule(rawRule)
        i++
    }
    i++
    val messages = rawLines.slice(i until rawLines.size)
    return rules to messages
}

fun main() {
    var (rules, messages) = readInput()
    println(rules)
    println(messages)
    rules["0"]!!.setupMatches(rules)

    println("There are ${messages.filter { it in rules["0"]!!.matches }.count()} matches")

    // Part 2
    println(rules["42"]!!.matches.sorted())
    println(rules["31"]!!.matches.sorted())
    println(rules["31"]!!.matches.intersect(rules["42"]!!.matches))
    val validPart2Messages = messages.filter { message ->
        val parts = message.chunked(8)
        var count42s = 0
        while (count42s < parts.size && parts[count42s] in rules["42"]!!.matches) {
            count42s++
        }
        var count31s = 0
        while (count42s + count31s < parts.size && parts[count42s + count31s] in rules["31"]!!.matches) {
            count31s++
        }
        count31s < count42s && count31s > 0 && count31s + count42s == parts.size
    }
    println("There are ${validPart2Messages.size} part 2 messages")
}