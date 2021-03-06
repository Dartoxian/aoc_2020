package aoc_14

import java.io.File


class Machine {
    var mask = "";
    val memory = mutableMapOf<Long, Long>()

    fun executeInstructionDecoder1(rawInstruction: String) {
        if (rawInstruction.startsWith("mask")) {
            updateMask(rawInstruction.substring(7))
        } else if (rawInstruction.startsWith("mem")) {
            val address = rawInstruction.subSequence((rawInstruction.indexOf('[')+1) until rawInstruction.indexOf(']')).toString().toLong()
            val unmaskedValue = rawInstruction.substring(rawInstruction.indexOf("= ") + 2).toLong()

            memory[address] = applyMask(unmaskedValue)
        } else {
            throw IllegalArgumentException("Cannot parse $rawInstruction")
        }
    }

    fun executeInstructionDecoder2(rawInstruction: String) {
        if (rawInstruction.startsWith("mask")) {
            updateMask(rawInstruction.substring(7))
        } else if (rawInstruction.startsWith("mem")) {
            val unmaskedAddress = rawInstruction.subSequence((rawInstruction.indexOf('[')+1) until rawInstruction.indexOf(']')).toString().toLong()
            val value = rawInstruction.substring(rawInstruction.indexOf("= ") + 2).toLong()

            applyMemoryMask(unmaskedAddress).forEach {address -> memory[address] = value }
        } else {
            throw IllegalArgumentException("Cannot parse $rawInstruction")
        }
    }

    private fun updateMask(newMask: String) {
        if (!newMask.matches("""^[X10]+$""".toRegex())) {
            throw IllegalArgumentException("Cannot use $newMask as a mask")
        }
        mask = newMask
    }

    private fun applyMask(toValue: Long): Long {
        val keepBitMask = mask
            .replace("0|1".toRegex(), "0")
            .replace('X', '1').toLong(2)
        val forceBitVal = mask
            .replace('X', '0').toLong(2)
        return toValue.and(keepBitMask) + forceBitVal
    }

    private fun applyMemoryMask(toValue: Long): List<Long> {
        val orBitMask = mask
            .replace('X', '0').toLong(2)

        return toValue.or(orBitMask).toString(2).padStart(36, '0')
            .zip(mask)
            .map { (v, m) -> if (m=='X') listOf('0', '1') else listOf(v) }
            .fold(listOf(""), {acc, cur -> acc.flatMap { p -> cur.map { c -> p + c } }})
            .map { p -> p.toLong(2) }
    }
}

fun part1() {
    val machine = Machine()
    File(ClassLoader.getSystemResource("aoc_14_input.txt").file)
        .readLines()
        .forEach { line -> machine.executeInstructionDecoder1(line) }

    println("Final Memory Sum is ${machine.memory.values.sum()}")
}

fun part2() {
    println("Part 2")
    val machine = Machine()
    File(ClassLoader.getSystemResource("aoc_14_input.txt").file)
        .readLines()
        .forEach { line -> machine.executeInstructionDecoder2(line) }

    println("Final Memory Sum is ${machine.memory.values.sum()}")
}

fun main() {
    part1()
    println()
    part2()
}