package aoc_11

import java.io.File
import kotlin.IllegalArgumentException

enum class TileType {
    FLOOR, SEAT_UNOCCUPIED, SEAT_OCCUPIED;

    override fun toString(): String = when(this) {
        FLOOR -> "."
        SEAT_UNOCCUPIED -> "L"
        SEAT_OCCUPIED -> "#"
    }

    fun isSeat(): Boolean = when(this) {
        FLOOR -> false
        SEAT_OCCUPIED -> true
        SEAT_UNOCCUPIED -> true
    }

    fun nextState(neighbours: Collection<TileType>) = when(this) {
        FLOOR -> FLOOR
        SEAT_UNOCCUPIED -> if (SEAT_OCCUPIED !in neighbours) SEAT_OCCUPIED else SEAT_UNOCCUPIED
        SEAT_OCCUPIED -> if (neighbours.filter { it == SEAT_OCCUPIED }.count() >= 4) SEAT_UNOCCUPIED else SEAT_OCCUPIED
    }

    fun nextStateTolerant(neighbours: Collection<TileType>) = when(this) {
        FLOOR -> FLOOR
        SEAT_UNOCCUPIED -> if (SEAT_OCCUPIED !in neighbours) SEAT_OCCUPIED else SEAT_UNOCCUPIED
        SEAT_OCCUPIED -> if (neighbours.filter { it == SEAT_OCCUPIED }.count() >= 5) SEAT_UNOCCUPIED else SEAT_OCCUPIED
    }
}

fun Char.toTile(): TileType = when(this) {
    '.' -> TileType.FLOOR
    'L' -> TileType.SEAT_UNOCCUPIED
    '#' -> TileType.SEAT_OCCUPIED
    else -> throw IllegalArgumentException("Cannot parse $this as a tile type")
}

data class Floorplan(val grid: List<List<TileType>>) {
    private val width: Int = grid[0].size
    val height: Int = grid.size
    val occupiedSeats: Int by lazy { grid.sumBy { row -> row.filter { tile -> tile == TileType.SEAT_OCCUPIED }.count() } }

    init {
        if (grid.any { row -> row.size != width }) {
            throw IllegalArgumentException("Grid passed to Floorplan is uneven")
        }
    }

    fun next(): Floorplan = Floorplan(grid.mapIndexed {i, row -> row.mapIndexed { j, tile ->
        tile.nextState(neighours(i, j).toList())
    }})

    fun nextPart2(): Floorplan = Floorplan(grid.mapIndexed {i, row -> row.mapIndexed { j, tile ->
        tile.nextStateTolerant(visibleNeighbours(i, j).toList())
    }})

    fun getTileAt(row: Int, col: Int): TileType? {
        if (row !in 0 until height || col !in 0 until width) {
            return null
        }
        return grid[row][col]
    }

    fun neighours(row: Int, col: Int): Sequence<TileType> = sequence {
        yield(getTileAt(row - 1, col - 1))
        yield(getTileAt(row - 1, col))
        yield(getTileAt(row - 1, col + 1))
        yield(getTileAt(row, col + 1))
        yield(getTileAt(row + 1, col + 1))
        yield(getTileAt(row + 1, col))
        yield(getTileAt(row + 1, col - 1))
        yield(getTileAt(row, col - 1))
    }.filterNotNull()

    fun getTileVisibleAt(row: Int, col: Int, rowTrajectory: Int, colTrajectory: Int): TileType? {
        val targetRow = row + rowTrajectory
        val targetCol = col + colTrajectory
        if (targetRow !in 0 until height || targetCol !in 0 until width) {
            return null
        }
        val targetTile = grid[targetRow][targetCol]
        return if (targetTile.isSeat()) targetTile else getTileVisibleAt(targetRow, targetCol, rowTrajectory, colTrajectory)
    }

    fun visibleNeighbours(row: Int, col: Int): Sequence<TileType> = sequence {
        yield(getTileVisibleAt(row, col, -1, -1))
        yield(getTileVisibleAt(row, col, -1, 0))
        yield(getTileVisibleAt(row, col, -1, 1))
        yield(getTileVisibleAt(row, col, 0, 1))
        yield(getTileVisibleAt(row, col, 1, 1))
        yield(getTileVisibleAt(row, col, 1, 0))
        yield(getTileVisibleAt(row, col, 1, -1))
        yield(getTileVisibleAt(row, col, 0, -1))
    }.filterNotNull()

    override fun toString(): String = grid.joinToString("\n") { row -> row.joinToString("") }
}

fun readInput(): Floorplan = File(ClassLoader.getSystemResource("aoc_11_input.txt").file)
    .readLines()
    .map { line -> line.map(Char::toTile) }
    .let { Floorplan(it) }

fun part1() {
    var currentFloorplan = readInput()
    println("Initial flooplan\n$currentFloorplan")

    var nextFloorplan = currentFloorplan.next()
    while (currentFloorplan != nextFloorplan) {
        currentFloorplan = nextFloorplan
        nextFloorplan = currentFloorplan.next()
    }
    println()
    println("Stable floorplan found, with ${currentFloorplan.occupiedSeats} occupied seats")
    println(currentFloorplan)
}

fun part2() {
    var currentFloorplan = readInput()
    println("Initial flooplan\n$currentFloorplan")

    var nextFloorplan = currentFloorplan.nextPart2()
    while (currentFloorplan != nextFloorplan) {
        currentFloorplan = nextFloorplan
        nextFloorplan = currentFloorplan.nextPart2()
    }
    println()
    println("Stable floorplan found, with ${currentFloorplan.occupiedSeats} occupied seats")
    println(currentFloorplan)
}

fun main() {
    part1()
    println()
    part2()
}