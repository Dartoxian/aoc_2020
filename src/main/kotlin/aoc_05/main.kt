package aoc_05

import java.io.File

data class BoardingPass(val row: Int, val col: Int) {
    val seatId by lazy {(row * 8) + col}

    companion object {
        fun parse(bspCode: String): BoardingPass {
            val matches = "([FB]{7})([LR]{3})".toRegex().matchEntire(bspCode)
                ?: throw IllegalArgumentException("The code $bspCode is not a valid boarding pass")

            val row = matches.groups[1]!!.value
                .replace('F', '0').replace('B', '1').toInt(2)
            val col = matches.groups[2]!!.value
                .replace('L', '0').replace('R', '1').toInt(2)

            return BoardingPass(row, col)
        }
    }

    override fun toString(): String {
        return "Row $row, Column $col, Seat ID $seatId"
    }
}

fun readInput(): List<BoardingPass> = File(ClassLoader.getSystemResource("aoc_05_input.txt").file)
    .readLines()
    .map(BoardingPass::parse)

fun main() {
    val passes = readInput()
    println("The highest seat id is ${passes.maxByOrNull { it.seatId }}")

    val (prev, _) = passes.sortedBy { it.seatId }
        .zipWithNext()
        .find { (prev, next) -> prev.seatId + 1 != next.seatId }
        ?: throw IllegalArgumentException("No absent seat could be found")
    println("The ID if your seat is ${prev.seatId + 1}")
}
