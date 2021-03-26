package aoc_18

import java.io.File
import java.lang.IllegalArgumentException

interface Equation {
    fun resolve(): Long
}

class ValueEquation(val value: Long): Equation {
    override fun resolve(): Long = value
}


class BinaryEquation(val left: Equation, val right: Equation, val operation: Char): Equation {
    override fun resolve(): Long = when(operation) {
        '+' -> left.resolve() + right.resolve()
        '*' -> left.resolve() * right.resolve()
        else -> throw IllegalArgumentException("Unable to resolve the operation '$operation'")
    }
}

fun tokenise(input: String): List<String> = input.split(" ")
    .flatMap { t ->
        if (t == "*" || t == "+") {
            listOf(t)
        } else {
            var i = 0
            val result = mutableListOf<String>()
            while (i < t.length && t[i] == '(') {
                result.add("(")
                i += 1
            }
            val digitStart = i
            while (i < t.length && t[i].isDigit()) {
                i += 1
            }
            result.add(t.substring(digitStart until i))
            while (i < t.length && t[i] == ')') {
                result.add(")")
                i += 1
            }
            result
        }
    }

fun readInput(parser: (tokens: List<String>) -> Equation): List<Equation> = File(ClassLoader.getSystemResource("aoc_18_input.txt").file)
    .readLines()
    .map(::tokenise)
    .map(parser)

fun parseL2Rpriority(tokens: List<String>): Equation {
    var i = 0
    fun recursiveParser(): Equation {
        var result = when(tokens[i]) {
            "(" -> {
                i++
                recursiveParser()
            }
            else -> {
                i++
                ValueEquation(tokens[i - 1].toLong())
            }
        }
        while (i < tokens.size) {
            if (tokens[i] == ")") {
                i++
                return result
            }
            val operation = tokens[i][0]
            i++
            result = BinaryEquation(result, when(tokens[i]) {
                "(" -> {
                    i++
                    recursiveParser()
                }
                else -> {
                    i++
                    ValueEquation(tokens[i - 1].toLong())
                }
            }, operation)
        }
        return result
    }
    return recursiveParser()
}

fun parseAdditionPriorityOverMultiplication(tokens: List<String>): Equation {
    var i = 0
    fun recursiveParser(): Equation {
        var result = mutableListOf<Pair<Char?, Equation>>()

        result.add(null to when(tokens[i]) {
            "(" -> {
                i++
                recursiveParser()
            }
            else -> {
                i++
                ValueEquation(tokens[i - 1].toLong())
            }
        })
        while (i < tokens.size) {
            if (tokens[i] == ")") {
                i++
                break
            }
            val operation = tokens[i][0]
            i++
            result.add(operation to when(tokens[i]) {
                "(" -> {
                    i++
                    recursiveParser()
                }
                else -> {
                    i++
                    ValueEquation(tokens[i - 1].toLong())
                }
            })
        }
        for (op in listOf('+', '*')) {
            val nextResult = mutableListOf(result[0])
            for (j in 1 until result.size) {
                if (result[j].first == op) {
                    nextResult[nextResult.lastIndex] =
                        nextResult.last().first to BinaryEquation(nextResult.last().second, result[j].second, op)
                } else {
                    nextResult.add(result[j])
                }
            }
            result = nextResult
        }
        return result[0].second
    }
    return recursiveParser()
}

fun main() {
    val part1Result = readInput(::parseL2Rpriority).map { it.resolve() }.sum()
    println("The answer for part one is $part1Result")

    val part2Result = readInput(::parseAdditionPriorityOverMultiplication).map { it.resolve() }.sum()
    println("The answer for part two is $part2Result")
}