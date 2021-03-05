package aoc_12

import java.io.File
import kotlin.math.absoluteValue

enum class Operation{
    NORTH, EAST, SOUTH, WEST, LEFT, RIGHT, FORWARD;

    private val directions by lazy { listOf(NORTH, EAST, SOUTH, WEST) }

    fun rotateLeft(amount: Int): Operation = when(this) {
        in directions -> directions[(4 + directions.indexOf(this) - (amount / 90)) % 4]
        else -> throw IllegalArgumentException("Cannot rotate $this")
    }

    fun rotateRight(amount: Int): Operation = when(this) {
        in directions -> directions[(4 + directions.indexOf(this) + (amount / 90)) % 4]
        else -> throw IllegalArgumentException("Cannot rotate $this")
    }
}

fun Char.toOperation(): Operation = when(this) {
    'N' -> Operation.NORTH
    'E' -> Operation.EAST
    'S' -> Operation.SOUTH
    'W' -> Operation.WEST
    'L' -> Operation.LEFT
    'R' -> Operation.RIGHT
    'F' -> Operation.FORWARD
    else -> throw IllegalArgumentException("Cannot convert $this to an Operation")
}

data class Instruction(val op: Operation, val quantity: Int)

fun String.parseInstruction(): Instruction {
    val matches = """(\w)(\d+)""".toRegex().matchEntire(this)
        ?: throw java.lang.IllegalArgumentException("Cannot parse '$this' as an instruction")
    return Instruction(matches.groupValues[1][0].toOperation(), matches.groupValues[2].toInt())
}

data class Waypoint(val east: Int = 0, val north: Int = 0) {
    fun updateWith(instruction: Instruction): Waypoint = when(instruction.op) {
        Operation.NORTH -> Waypoint(east, north + instruction.quantity)
        Operation.EAST -> Waypoint(east + instruction.quantity, north)
        Operation.SOUTH -> Waypoint(east, north - instruction.quantity)
        Operation.WEST -> Waypoint(east - instruction.quantity, north)
        Operation.RIGHT -> rotateRight(instruction.quantity)
        Operation.LEFT -> rotateRight(360 - instruction.quantity)
        Operation.FORWARD -> throw IllegalArgumentException("Waypoints cannot be updated FORWARD")
    }

    fun rotateRight(amount: Int): Waypoint {
        var result = this
        var rotated = 0
        while (rotated < amount) {
            result = Waypoint(result.north, -result.east)
            rotated += 90
        }
        return result
    }
}

data class Position(val east: Int = 0, val north: Int = 0, val orientation: Operation = Operation.EAST) {
    val manhattanDistance by lazy { east.absoluteValue + north.absoluteValue }

    fun updateWith(instruction: Instruction): Position = when(instruction.op) {
        Operation.NORTH -> Position(east, north + instruction.quantity, orientation)
        Operation.EAST -> Position(east + instruction.quantity, north, orientation)
        Operation.SOUTH -> Position(east, north - instruction.quantity, orientation)
        Operation.WEST -> Position(east - instruction.quantity, north, orientation)
        Operation.RIGHT -> Position(east, north, orientation.rotateRight(instruction.quantity))
        Operation.LEFT -> Position(east, north, orientation.rotateLeft(instruction.quantity))
        Operation.FORWARD -> updateWith(Instruction(orientation, instruction.quantity))
    }

    fun moveTowards(waypoint: Waypoint, amount: Int): Position =
        Position(east + (waypoint.east * amount), north + (waypoint.north * amount))
}

fun part1(instructions: List<Instruction>) {
    var position = Position()
    instructions.forEach { instruction -> position = position.updateWith(instruction) }
    println("Final manhattan of part 1 distance is ${position.manhattanDistance}")
}

fun part2(instructions: List<Instruction>) {
    var position = Position()
    var waypoint = Waypoint(10, 1)
    instructions.forEach { instruction ->
        if (instruction.op == Operation.FORWARD) {
            position = position.moveTowards(waypoint, instruction.quantity)
        } else {
            waypoint = waypoint.updateWith(instruction)
        }
    }
    println("Final manhattan distance of part 2 is ${position.manhattanDistance}")
}

fun main() {
    val instructions = File(ClassLoader.getSystemResource("aoc_12_input.txt").file)
        .readLines()
        .map(String::parseInstruction)

    part1(instructions)
    part2(instructions)
}