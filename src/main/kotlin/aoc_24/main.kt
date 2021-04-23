package aoc_24

import java.io.File
import java.lang.IllegalArgumentException

fun getNeighbours(x: Double, y: Double): Set<Pair<Double, Double>> = setOf(
    x - 0.5 to y + 1,
    x + 0.5 to y + 1,
    x + 1 to y,
    x + 0.5 to y - 1,
    x - 0.5 to y - 1,
    x - 1 to y,
)

class Tiles {
    private var blackTiles: MutableSet<Pair<Double, Double>> = mutableSetOf()

    fun applyInstruction(rawInstruction: String) {
        var x = 0.0
        var y = 0.0
        var i = 0
        while (i < rawInstruction.length) {
            if (rawInstruction[i] in setOf('s', 'n')) {
                when(rawInstruction[i]) {
                    's' -> y--
                    'n' -> y++
                }
                i++
                if (rawInstruction[i] == 'e') {
                    x += 0.5
                } else {
                    x -= 0.5
                }
            } else if (rawInstruction[i] in setOf('w', 'e')) {
                when(rawInstruction[i]) {
                    'w' -> x--
                    'e' -> x++
                }
            } else {
                throw IllegalArgumentException("Unable to parse instruction $rawInstruction on char $i")
            }
            i++
        }
        if (x to y in blackTiles) {
            blackTiles.remove(x to y)
        } else {
            blackTiles.add(x to y)
        }
    }

    fun nextDay() {
        blackTiles = (blackTiles.filter { (x, y) ->
            val numNeighboursBlack = getNeighbours(x, y).intersect(blackTiles).size
            numNeighboursBlack == 1 || numNeighboursBlack == 2
        } + blackTiles.flatMap { (x, y) ->
            getNeighbours(x, y)
                .filter { it !in blackTiles && getNeighbours(it.first, it.second).intersect(blackTiles).size == 2 }
        }).toMutableSet()
    }

    fun countBlackTiles() = blackTiles.size
}

fun readInput(): List<String> =File(ClassLoader.getSystemResource("aoc_24_input.txt").file)
        .readLines()

fun main() {
    val rawInstructions = readInput()
    val tiles = Tiles()
    rawInstructions.forEach(tiles::applyInstruction)
    println("After following the instructions there are ${tiles.countBlackTiles()} black tiles.")

    (1..100).forEach { day ->
        tiles.nextDay()
        println("Day $day: ${tiles.countBlackTiles()}")
    }
}