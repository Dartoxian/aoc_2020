package aoc_25

fun transform(subjectNumber: Long, loopSize: Long): Long {
    var value = 1L
    var i = 0
    while (i < loopSize) {
        value *= subjectNumber
        value %= 20201227
        i++
    }
    return value
}

fun findLoopSize(subjectNumber: Long, publicKey: Long): Long {
    var value = 1L
    var i = 0L
    while (publicKey != value) {
        value *= subjectNumber
        value %= 20201227
        i++
    }
    return i
}

fun handshake(cardLoopSize: Long, doorLoopSize: Long) {
    val cardPublicKey = transform(7, cardLoopSize)
    val doorPublicKey = transform(7, doorLoopSize)

}

fun main() {
    val cardPublicKey = 10604480L
    val doorPublicKey = 4126658L

    var cardLoopSize = findLoopSize(7, cardPublicKey)
    println("Card loop size is $cardLoopSize")

    var doorLoopSize = findLoopSize(7, doorPublicKey)
    println("Door loop size is $doorLoopSize")

    var encryptionKey = transform(cardPublicKey, doorLoopSize)
    println("Encryption key is $encryptionKey")
}