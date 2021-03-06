package aoc_15

fun main() {
    val init = "0,1,5,10,3,12,19".split(",").map(String::toLong)
    val lastOccurence = mutableMapOf<Long, Long>(init[0] to 1L)

    val sequence = generateSequence(init[0] to 1L, {(lastValue, lastIndex) ->
        val nextIndex = lastIndex + 1
        val nextValue = init.getOrNull((nextIndex - 1).toInt())
            ?: if (lastValue in lastOccurence) lastIndex - lastOccurence[lastValue]!! else 0
        lastOccurence[lastValue] = lastIndex
        nextValue to nextIndex
    }).map { (value, _) -> value }

    //println(sequence.take(2020).last())
    println(sequence.take(30000000).last())
}