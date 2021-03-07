package aoc_17

import java.io.File



fun getNeighbours(cube: List<Long>): Set<List<Long>> {
    fun getTouchingCubes(cube: List<Long>): Collection<List<Long>> =
        if (cube.size == 1)
                (-1..1).map { listOf(cube[0] + it) }
        else (-1..1).flatMap { offset ->
            getTouchingCubes(cube.slice(1..cube.lastIndex))
                .map { listOf(offset.toLong() + cube[0]) + it }
    }
    return getTouchingCubes(cube).filter { it != cube }.toSet()
}

fun permuteCubes(input: Set<List<Long>>): Set<List<Long>> = input
    .flatMap { cube -> getNeighbours(cube) + setOf(cube) }
    .toSet()
    .map { cube -> when(cube) {
        in input -> if (getNeighbours(cube).intersect(input).size in 2..3) cube else null
        else -> if (getNeighbours(cube).intersect(input).size == 3) cube else null
    } }
    .filterNotNull()
    .toSet()


fun readInput(dimensionality: Int): Set<List<Long>> = File(ClassLoader.getSystemResource("aoc_17_input.txt").file)
    .readLines()
    .flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, cell ->
            if (cell == '.') null else listOf(x.toLong(), y.toLong()) + (1..(dimensionality - 2)).map { 0L }
        }
    }.toSet()

fun main() {
    var activeCubes = readInput(3)
    println(activeCubes)

    (1..6).forEach{ activeCubes = permuteCubes(activeCubes) }
    println("After 6 rounds in 3 dimensions there are ${activeCubes.size} active cubes.")

    activeCubes = readInput(4)

    (1..6).forEach{ activeCubes = permuteCubes(activeCubes) }
    println("After 6 rounds in 4 dimensions there are ${activeCubes.size} active cubes.")
}