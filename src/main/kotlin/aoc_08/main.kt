package aoc_08

import java.io.File

enum class Operation {
    ACC, JMP, NOP
}

data class Instruction(val operation: Operation, val argument: Int) {
    fun flipped(): Instruction = when(operation) {
        Operation.ACC -> throw IllegalStateException("Cannot flip the ACC operation")
        Operation.JMP -> Instruction(Operation.NOP, argument)
        Operation.NOP -> Instruction(Operation.JMP, argument)
    }
}

fun parseInstruction(raw: String): Instruction {
    val matches = """(\w{3}) ([+-]\d+)""".toRegex().matchEntire(raw)
        ?: throw IllegalStateException("Cannot parse $raw")
    return Instruction(
        Operation.valueOf(matches.groupValues[1].toUpperCase()),
        matches.groupValues[2].toInt()
    )
}

class Machine(val program: List<Instruction>) {
    private var acc: Int = 0
    private var pc: Int = 0
    private var visitedPC = mutableSetOf<Int>()

    fun finished(): Boolean = pc == program.size

    fun step() {
        if (pc in visitedPC) {
            throw IllegalStateException("Cannot execute $pc, as it was already executed (inifinite loop detected).")
        }
        visitedPC.add(pc)
        when(program[pc].operation) {
            Operation.ACC -> {
                acc += program[pc].argument
                pc += 1
            }
            Operation.JMP -> {
                pc += program[pc].argument
            }
            Operation.NOP -> {
                pc += 1
            }
        }
    }

    override fun toString(): String {
        return "Current Machine State: [PC: $pc, ACC: $acc]"
    }
}

fun readInput(): List<Instruction> = File(ClassLoader.getSystemResource("aoc_08_input.txt").file)
        .readLines()
        .map(::parseInstruction)

fun main() {
    val instructions = readInput()
    val machine = Machine(instructions)
    try {
        while (!machine.finished()) {
            machine.step()
        }
    } catch (e: IllegalStateException) {
        println("Machine failed with state: ")
        println(machine)
    }

    println()
    println()

    for (i in instructions.indices) {
        if (instructions[i].operation == Operation.ACC) {
            continue
        }
        val modifiedInstructions = instructions.mapIndexed { instructionIndex, instruction ->
            if (instructionIndex == i) instruction.flipped() else instruction
        }
        val modifiedMachine = Machine(modifiedInstructions)
        try {
            while (!modifiedMachine.finished()) {
                modifiedMachine.step()
            }
        } catch (e: IllegalStateException) {
            continue
        }
        println("Modifying instruction $i allows the program to complete successfully")
        println("Final State:")
        print(modifiedMachine)
        break
    }
}