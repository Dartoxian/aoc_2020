package aoc_23

data class CupConfiguration(val cups: List<Int>) {
    fun nextCupConfiguration(): CupConfiguration {
        val nextThreeCups = cups.slice(1..3)
        println("pick: ${nextThreeCups.joinToString(" ")}")
        var destinationCupLabel = cups[0] - 1
        if (destinationCupLabel == 0) {
            destinationCupLabel = cups.size
        }
        while (destinationCupLabel in nextThreeCups) {
            destinationCupLabel = destinationCupLabel - 1
            if (destinationCupLabel == 0) {
                destinationCupLabel = cups.size
            }
        }
        println("destination: $destinationCupLabel")
        val destinationCupLocation = cups.indexOf(destinationCupLabel)

        return CupConfiguration(
            cups.slice(4..destinationCupLocation)
                    + nextThreeCups
                    + cups.slice((destinationCupLocation + 1) until cups.size)
                    + listOf(cups[0])
        )
    }

    override fun toString(): String = "cups: (${cups[0]}) ${cups.slice(1 until cups.size).joinToString(" ")}"
}

data class Cup(val label: Int) {
    lateinit var nextCup: Cup
}

class MutatingCupConfiguration {
    lateinit var currentCup: Cup
    private var cupLookup: MutableMap<Int, Cup> = mutableMapOf()
    constructor(initialCups: List<Int>) {
        var prevCup: Cup? = null
        initialCups.forEach{ label ->
            val cup = Cup(label)
            prevCup?.nextCup = cup
            cupLookup[label] = cup
            if (prevCup == null) {
                currentCup = cup
            }
            prevCup = cup
        }
        prevCup?.nextCup = currentCup
        println("Initialised with ${cupLookup.size} cups")
        println("Current cup is ${currentCup.label}")
    }

    fun stepCupConfiguration() {
        val nextThreeCupLabels = setOf(
            currentCup.nextCup.label,
            currentCup.nextCup.nextCup.label,
            currentCup.nextCup.nextCup.nextCup.label,
        )
        var destinationCupLabel = currentCup.label - 1
        if (destinationCupLabel == 0) {
            destinationCupLabel = cupLookup.size
        }
        while (destinationCupLabel in nextThreeCupLabels) {
            destinationCupLabel = destinationCupLabel - 1
            if (destinationCupLabel == 0) {
                destinationCupLabel = cupLookup.size
            }
        }
        val destinationCup = cupLookup[destinationCupLabel]!!
        val oldFifthCup = currentCup.nextCup.nextCup.nextCup.nextCup
        currentCup.nextCup.nextCup.nextCup.nextCup = destinationCup.nextCup
        destinationCup.nextCup = currentCup.nextCup

        val oldCurrentCup = currentCup
        currentCup = oldFifthCup
        oldCurrentCup.nextCup = currentCup
    }

    fun findStars() {
        val afterOne = cupLookup[1]!!.nextCup.label
        val afterAfterOne = cupLookup[1]!!.nextCup.nextCup.label
        println("Part two stars under $afterOne and $afterAfterOne")
        println("Creating result ${afterAfterOne.toLong() * afterOne.toLong()}")
    }
}

fun main() {
    var cupConfig = CupConfiguration("476138259".map { it.toString().toInt() })
    var moveId = 1
    while (moveId <= 100) {
        println("-- move $moveId --")
        println(cupConfig)
        cupConfig = cupConfig.nextCupConfiguration()
        println()
        moveId++
    }
    println("-- final --")
    println(cupConfig)
    println("The output for the problem is: ${cupConfig.run { 
        val oneAddress = cups.indexOf(1)
        (cups.slice((oneAddress + 1) until cups.size) + cups.slice(0 until oneAddress)).joinToString("")
    }}")

    println()
    println("Part 2")
    println()

    val mutatingCupConfig = MutatingCupConfiguration("476138259".map { it.toString().toInt() } + (10..1000000).toList())
    moveId = 1
    while (moveId <= 10000000) {
        mutatingCupConfig.stepCupConfiguration()
        moveId++
        if (moveId % 1000000 == 0) {
            println("$moveId moves processed...")
        }
    }
    println("The output for part two of the problem is:")
    mutatingCupConfig.findStars()
    println(mutatingCupConfig.currentCup.label)
}