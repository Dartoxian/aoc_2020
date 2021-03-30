package aoc_20

import java.io.File

enum class Position {
    TOP, RIGHT, BOTTOM, LEFT
}

class Tile(val id: Long, val rawTileData: List<List<Char>>) {
    fun getRotations(): Sequence<Tile> = sequence {
        yield(this@Tile)
        var rotRaw = rawTileData
        for (i in 1..3) {
            rotRaw = (0 until rotRaw.size).map { i ->
                (0 until rotRaw[i].size).map { j ->
                    rotRaw[rotRaw.size - j - 1][i]
                }
            }
            yield(Tile(id, rotRaw))
        }
        rotRaw = rawTileData
        // mirrored
        rotRaw = (0 until rotRaw.size).map { i ->
            (0 until rotRaw[i].size).map { j ->
                rotRaw[i][rotRaw.size - j - 1]
            }
        }
        yield(Tile(id, rotRaw))
        for (i in 1..3) {
            rotRaw = (0 until rotRaw.size).map { i ->
                (0 until rotRaw[i].size).map { j ->
                    rotRaw[rotRaw.size - j - 1][i]
                }
            }
            yield(Tile(id, rotRaw))
        }
    }

    fun getBorder(position: Position): List<Char> = when(position) {
        Position.TOP -> rawTileData.first()
        Position.BOTTOM -> rawTileData.last()
        Position.LEFT -> rawTileData.map { it.first() }
        Position.RIGHT -> rawTileData.map { it.last() }
    }

    fun alignsWith(other: Tile, position: Position): Boolean = when(position) {
        Position.TOP -> getBorder(position) == other.getBorder(Position.BOTTOM)
        Position.BOTTOM -> getBorder(position) == other.getBorder(Position.TOP)
        Position.RIGHT -> getBorder(position) == other.getBorder(Position.LEFT)
        Position.LEFT -> getBorder(position) == other.getBorder(Position.RIGHT)
    }

    override fun toString(): String = StringBuilder().run {
        appendLine("Tile $id:")
        rawTileData.forEach { appendLine(it.joinToString("")) }
        toString()
    }

    override fun equals(other: Any?): Boolean = other is Tile && other.id == id
}

fun Tile.findNeighbour(position: Position, allTiles: Collection<Tile>): Tile? = allTiles
    .asSequence()
    .filter { it != this }
    .flatMap { it.getRotations() }
    .find { alignsWith(it, position) }

fun Tile.stripBorders(): List<List<Char>> = rawTileData
    .slice(1 until (rawTileData.size - 1)).map { it.slice(1 until (it.size - 1)) }

fun readInput(): Sequence<Tile> = sequence {
    val rawLines = File(ClassLoader.getSystemResource("aoc_20_input.txt").file)
        .readLines()
    var i = 0
    while (i < rawLines.size) {
        val id = rawLines[i].substringAfter(' ').substringBefore(':').toInt()
        i++
        val rawTileData = mutableListOf<List<Char>>()
        while (i < rawLines.size && rawLines[i] != "") {
            rawTileData.add(rawLines[i].toList())
            i++
        }
        yield(Tile(id.toLong(), rawTileData))
        i++
    }
}

fun main() {
    val tiles = readInput().toList()

    var t: Tile? = tiles[0]
    var leftBorder: Tile = t!!
    while (t != null) {
        leftBorder = t
        t = t.findNeighbour(Position.LEFT, tiles)
    }
    t = leftBorder
    var topLeft = leftBorder
    while (t != null) {
        topLeft = t
        t = t.findNeighbour(Position.TOP, tiles)
    }
    t = leftBorder
    var bottomLeft = leftBorder
    while (t != null) {
        bottomLeft = t
        t = t.findNeighbour(Position.BOTTOM, tiles)
    }
    t = topLeft
    var topRight = topLeft
    while (t != null) {
        topRight = t
        t = t.findNeighbour(Position.RIGHT, tiles)
    }
    t = bottomLeft
    var bottomRight = bottomLeft
    while (t != null) {
        bottomRight = t
        t = t.findNeighbour(Position.RIGHT, tiles)
    }

    val part1Answer = bottomLeft.id * bottomRight.id * topLeft.id * topRight.id
    println("Part 1 answer is $part1Answer")

    val arrangedImage = mutableListOf<List<Tile>>()
    val leftColumn = mutableListOf<Tile>(topLeft)
    var neighbour = leftColumn.last().findNeighbour(Position.BOTTOM, tiles)
    while (neighbour != null) {
        leftColumn.add(neighbour)
        neighbour = leftColumn.last().findNeighbour(Position.BOTTOM, tiles)
    }

    for (leftTile in leftColumn) {
        val row = mutableListOf(leftTile)
        neighbour = row.last().findNeighbour(Position.RIGHT, tiles)
        while (neighbour != null) {
            row.add(neighbour)
            neighbour = row.last().findNeighbour(Position.RIGHT, tiles)
        }
        arrangedImage.add(row)
    }
    val completedTile = Tile(0, arrangedImage.flatMap { row ->
        val strippedTiles = row.map { it.stripBorders() }
        (strippedTiles[0].indices).map { i -> strippedTiles.map { rawTile -> rawTile[i].joinToString("") }.joinToString("").toList() }
    })
    println("completed tile: $completedTile")

    val seamonsterImage = listOf(
        "                  # ".toList(),
        "#    ##    ##    ###".toList(),
        " #  #  #  #  #  #   ".toList(),
    )
    val seamonsterLocations = seamonsterImage.flatMapIndexed{ i, row ->
        row.mapIndexed { j, c -> if (c =='#') i to j else null }
    }.filterNotNull()
        .toSet()
    println("Seamonster locations $seamonsterLocations")

    fun Tile.hasSeamonsters(): Boolean = rawTileData.any { row -> 'O' in row }

    fun Tile.detectSeamonsterAt(i: Int, j: Int): Boolean = seamonsterLocations.all { (iOffset, jOffset) ->
        rawTileData.getOrNull(i + iOffset)?.getOrNull(j + jOffset) == '#'
    }

    println(Tile(123, seamonsterImage).detectSeamonsterAt(0, 0))

    fun Tile.markSeamonsterAt(i: Int, j: Int): Tile {
        val newData = rawTileData.map { row -> row.toMutableList() }.toMutableList()
        seamonsterLocations.forEach { (iOffset, jOffset) -> newData[i + iOffset][j + jOffset] = 'O' }
        return Tile(id, newData)
    }

    fun Tile.findSeamonsters(): Tile {
        var result = this
        rawTileData.indices.forEach { i -> rawTileData[i].indices.forEach { j ->
            if (result.detectSeamonsterAt(i, j)) {
                println("seamonster detected")
                result = result.markSeamonsterAt(i, j)
            }
        }}
        return result
    }

    fun Tile.getWaterRoughness(): Int = rawTileData.flatten().count { it == '#' }

    for (completedVariant in completedTile.getRotations()) {
        val detectedSeamonsters = completedVariant.findSeamonsters()
        if (detectedSeamonsters.hasSeamonsters()) {
            println("Found variant with seamonsters $detectedSeamonsters")
            println("with water roughness ${detectedSeamonsters.getWaterRoughness()}")
            break
        }
    }
}