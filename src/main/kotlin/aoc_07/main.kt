package aoc_07

import java.io.File

data class Bag(val colour: String, val children: MutableCollection<Pair<Bag, Int>> = mutableListOf()) {
    private val containsCache = mutableMapOf<Bag, Boolean>()

    operator fun contains(other: Bag): Boolean {
        if (other in containsCache) {
            return containsCache.getValue(other)
        }
        for ((child, count) in children) {
            if (child == other) {
                containsCache[other] = true
                return true
            }
            if (other in child) {
                containsCache[other] = true
                return true
            }
        }
        containsCache[other] = false
        return false
    }

    fun numberOfChildren(): Int {
        return children.map { (child, count) -> count + (count * child.numberOfChildren()) }.sum()
    }
}

fun parseRule(raw: String): Pair<String, Collection<Pair<String, Int>>> {
    val (target, children) = raw.split(" contain ")
    val targetColour = target.replace(" bags", "")
    val childColours = mutableListOf<Pair<String, Int>>()
    if (children != "no other bags.") {
        for (child in children.split(", ")) {
            val match = """(\d+) ([a-z ]+) bags?.?""".toRegex().matchEntire(child)
                ?: throw IllegalArgumentException("Cannot parse child '$child'")
            childColours.add(match.groups[2]!!.value to match.groups[1]!!.value.toInt())
        }
    }

    return targetColour to childColours
}

fun readInput(): Map<String, Bag> {
    val rules = File(ClassLoader.getSystemResource("aoc_07_input.txt").file)
        .readLines()
        .map(::parseRule)
    val bagLookup = mutableMapOf<String, Bag>()
    for (rule in rules) {
        if (rule.first !in bagLookup) {
            bagLookup[rule.first] = Bag(rule.first)
        }
        bagLookup[rule.first]!!.children.addAll(rule.second.map { (childColour, childCount) ->
            if (childColour !in bagLookup) {
                bagLookup[childColour] = Bag(childColour)
            }
            bagLookup[childColour]!! to childCount
        })
    }
    return bagLookup
}

fun main() {
    val bagsByName = readInput()
    val shinyGoldBag = bagsByName["shiny gold"] ?: throw IllegalArgumentException("No Shiny Gold Bag found in rules")
    val numBagsThatCanContainShinyGold = bagsByName.values.filter { bag -> shinyGoldBag in bag }.count()

    println("There are $numBagsThatCanContainShinyGold bags that can contain shiny gold bags.")
    println("There are ${shinyGoldBag.numberOfChildren()} bags inside a shiny gold bag.")
}
