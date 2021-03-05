package aoc_13

import java.io.File

fun timeUntil(time: Long, busId: Int): Long = busId - (time % busId)

class BusSchedule(val busId: Int) {
    fun nextDepartureTime(from: Long): Long {
        return busId * (from / busId)
    }

    override fun toString(): String = "Bus $busId"
}

fun Int?.toBusScheduleOrNull(): BusSchedule? = if (this != null) BusSchedule(this) else null

data class Schedule(val buses: List<BusSchedule?>) {
    fun findNextBus(time: Long): BusSchedule = buses.filterNotNull().minByOrNull { busId -> busId.nextDepartureTime(time) }
        ?: throw IllegalStateException("There are no buses in the schedule")

    fun busWithCompetitionOffset() = buses
        .mapIndexed { offset, bus -> if (bus != null) bus to offset % bus.busId else null}
        .filterNotNull()
}

fun readInput(): Pair<Long, Schedule> = File(ClassLoader.getSystemResource("aoc_13_input.txt").file)
    .readLines()
    .let { rawLines -> rawLines[0].toLong() to Schedule(rawLines[1].split(",").map { it.toIntOrNull().toBusScheduleOrNull() }) }

fun main() {
    val (currentTime, schedule) = readInput()
    val nextBus = schedule.findNextBus(currentTime)
    val timeUntilNextBus = timeUntil(currentTime, nextBus.busId)
    println("The next bus is ${nextBus.busId}, in $timeUntilNextBus minutes")
    println("The solution is ${nextBus.busId * timeUntilNextBus}")

    println()

    println("Buses and required schedule offset")
    println(schedule.busWithCompetitionOffset())
    schedule.busWithCompetitionOffset()
        .sortedBy { it.first.busId }
        .map { (bus, offset) -> println("(t + $offset) mod ${bus.busId} = 0     => t = -$offset mod ${bus.busId}") }

    // [(Bus 13, 0), (Bus 41, 3), (Bus 37, 7), (Bus 419, 13), (Bus 19, 13), (Bus 23, 13), (Bus 29, 13), (Bus 421, 44), (Bus 17, 10)]

    val competitionResult = generateSequence<Long>((19 * 23 * 29 * 419) -13, {it + (19 * 23 * 29 * 419)})
        .filter { it % 13 == 0L && it > 0 }
        .filter { (it + 10) % 17 == 0L }
        .filter { (it + 7) % 37 == 0L }
        .filter { (it + 3) % 41 == 0L }
        .filter { (it + 44) % 421 == 0L }
        .first()
    println("Competition Result is $competitionResult")

}