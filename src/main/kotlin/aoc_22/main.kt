package aoc_22

import java.io.File

data class Deck(val cards: List<Int>) {
    fun score(): Int = cards.reversed().mapIndexed {index, card -> (index + 1) * card}.sum()

    override fun toString(): String = cards.joinToString(", ")
}

data class CombatGameState(val p1: Deck, val p2: Deck, val round: Int = 1) {
    fun nextState(): CombatGameState {
        if (gameComplete()) throw IllegalAccessException("Cannot compute next state when the game is complete")

        println("-- Round $round --")
        println("Player 1's deck: $p1")
        println("Player 2's deck: $p2")
        val p1Card = p1.cards.first()
        val p2Card = p2.cards.first()
        println("Player 1 plays: $p1Card")
        println("Player 2 plays: $p2Card")
        if (p1Card > p2Card) {
            println("Player 1 wins the round!")
            return CombatGameState(
                Deck(p1.cards.slice(1 until p1.cards.size) + listOf(p1Card, p2Card)),
                Deck(p2.cards.slice(1 until p2.cards.size)),
                round + 1
            )
        } else {
            println("Player 2 wins the round!")
            return CombatGameState(
                Deck(p1.cards.slice(1 until p1.cards.size)),
                Deck(p2.cards.slice(1 until p2.cards.size) + listOf(p2Card, p1Card)),
                round + 1
            )
        }
    }

    fun gameComplete(): Boolean = p1.cards.isEmpty() || p2.cards.isEmpty()

    fun score(): Int {
        if (!gameComplete()) {
            throw IllegalAccessException("Cannot get game score when the game is not complete")
        }
        val deck = if (p1.cards.isEmpty()) p2 else p1
        return deck.score()
    }
}

var globalGameId = 1

data class RecursiveCombatGameState(
    val p1: Deck, val p2: Deck, val gameId: Int, val round: Int = 1, val knownStates: Set<Pair<Deck, Deck>> = setOf()
) {

    companion object {
        fun fromCombatGame(g: CombatGameState) = RecursiveCombatGameState(g.p1, g.p2, globalGameId++)
    }

    fun nextState(): RecursiveCombatGameState {
        if (gameComplete()) throw IllegalAccessException("Cannot compute next state when the game is complete")

        println("-- Round $round --")
        println("Player 1's deck: $p1")
        println("Player 2's deck: $p2")
        val p1Card = p1.cards.first()
        val p2Card = p2.cards.first()
        println("Player 1 plays: $p1Card")
        println("Player 2 plays: $p2Card")
        var p1Wins = p1Card > p2Card
        if (p1Card < p1.cards.size && p2Card < p2.cards.size) {
            println("Playing a sub-game to determine the winner...")
            println()
            val subgameId = globalGameId++
            println("=== Game $subgameId ===")
            println()
            var subGame = RecursiveCombatGameState(
                Deck(p1.cards.slice(1..p1Card)),
                Deck(p2.cards.slice(1..p2Card)),
                subgameId,
            )
            while (!subGame.gameComplete()) {
                subGame = subGame.nextState()
                println()
            }
            p1Wins = subGame.isP1Winner()
            println()
            println("...anyway, back to game $gameId")
        }

        if (p1Wins) {
            println("Player 1 wins round $round of game $gameId!")
            return RecursiveCombatGameState(
                Deck(p1.cards.slice(1 until p1.cards.size) + listOf(p1Card, p2Card)),
                Deck(p2.cards.slice(1 until p2.cards.size)),
                gameId,
                round + 1,
                knownStates + setOf(p1 to p2)
            )
        } else {
            println("Player 2 wins the round!")
            return RecursiveCombatGameState(
                Deck(p1.cards.slice(1 until p1.cards.size)),
                Deck(p2.cards.slice(1 until p2.cards.size) + listOf(p2Card, p1Card)),
                gameId,
                round + 1,
                knownStates + setOf(p1 to p2)
            )
        }
    }

    fun gameComplete(): Boolean = (p1 to p2 in knownStates) || p1.cards.isEmpty() || p2.cards.isEmpty()

    fun isP1Winner(): Boolean = (p1 to p2 in knownStates) || p2.cards.isEmpty()

    fun score(): Int {
        if (!gameComplete()) {
            throw IllegalAccessException("Cannot get game score when the game is not complete")
        }
        val deck = if (p1.cards.isEmpty()) p2 else p1
        return deck.score()
    }
}


fun readInput(): CombatGameState {
    val lines = File(ClassLoader.getSystemResource("aoc_22_input.txt").file)
        .readLines()

    var i = 1
    val p1Deck = mutableListOf<Int>()
    val p2Deck = mutableListOf<Int>()
    while (lines[i] != "") {
        p1Deck.add(lines[i].toInt())
        i++
    }
    i++
    i++
    while (i < lines.size && lines[i] != "") {
        p2Deck.add(lines[i].toInt())
        i++
    }
    return CombatGameState(Deck(p1Deck), Deck(p2Deck))
}

fun main() {
    var gameState = readInput()
    while (!gameState.gameComplete()) {
        gameState = gameState.nextState()
        println()
    }
    println("The final score of the victor was ${gameState.score()}")

    gameState = readInput()
    var recursiveGameState = RecursiveCombatGameState.fromCombatGame(gameState)
    while (!recursiveGameState.gameComplete()) {
        recursiveGameState = recursiveGameState.nextState()
        println()
    }
    println("The final score of the victor was ${recursiveGameState.score()}")
}