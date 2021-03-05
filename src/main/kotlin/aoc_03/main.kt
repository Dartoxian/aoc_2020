package aoc_03

import java.io.File

enum class Tile {
    OPEN,
    TREE;
}

data class LandscapeRow(val tiles: List<Tile>) {
    companion object {
        fun parseRow(s: String): LandscapeRow {
            return LandscapeRow(s.map { when(it){
                '.' -> Tile.OPEN
                '#' -> Tile.TREE
                else -> throw IllegalArgumentException("Cannot parse $s as a row")
            } })
        }
    }

    fun getTileAt(position: Int): Tile = tiles[position % tiles.size]
}

data class SlopeStrategy(val down: Int, val right: Int)

infix fun Int.downRight(other: Int): SlopeStrategy = SlopeStrategy(this, other)

data class Landscape(val rows: List<LandscapeRow>) {
    fun treesHitWithStrategy(strategy: SlopeStrategy): Int =
        (rows.indices step strategy.down).map { rowNumber ->
            when(rows[rowNumber].getTileAt(rowNumber * strategy.right / strategy.down)) {
                Tile.OPEN -> 0
                Tile.TREE -> 1
            }
        }.sum()
}

fun readInput(): Landscape = File(ClassLoader.getSystemResource("aoc_03_input.txt").file)
    .readLines()
    .map(LandscapeRow.Companion::parseRow)
    .let { Landscape(it) }

fun main() {
    val landscape = readInput()

    val treesHit = landscape.treesHitWithStrategy(1 downRight 3)
    println("There are $treesHit trees hit with initial strategy")

    val strategies = listOf(
        1 downRight 1,
        1 downRight 3,
        1 downRight 5,
        1 downRight 7,
        2 downRight 1
    )
    val product = strategies.map(landscape::treesHitWithStrategy).reduce{a, b -> a * b}
    println("The second solution is $product")
}