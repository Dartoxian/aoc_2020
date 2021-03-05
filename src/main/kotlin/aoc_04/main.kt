package aoc_04

import java.io.File

val VALID_EYE_COLOURS = setOf(
    "amb", "blu", "brn", "gry", "grn", "hzl", "oth"
)

data class Height(val height: Int, val units: String) {
    fun isValid(): Boolean =
        (units == "cm" && height in 150..193) ||
                (units == "in" &&  height in 59..76)
}

data class Passport(
    val byr: String?,// (Birth Year)
    val iyr: String?,// (Issue Year)
    val eyr: String?,// (Expiration Year)
    val hgt: String?,// (Height)
    val hcl: String?,// (Hair Color)
    val ecl: String?,// (Eye Color)
    val pid: String?,// (Passport ID)
    val cid: String?,// (Country ID)
) {
    fun fieldsPresent(): Boolean {
        return !byr.isNullOrBlank() && !iyr.isNullOrBlank() && !eyr.isNullOrBlank() && !hgt.isNullOrBlank()
                && !hcl.isNullOrBlank() && !ecl.isNullOrBlank() && !pid.isNullOrBlank()
    }

    fun String.asHeight(): Height? = """(\d+)(cm|in)""".toRegex()
        .matchEntire(this)
        ?.run { Height(groups[1]!!.value.toInt(), groups[2]!!.value)}

    fun String.isColour(): Boolean = this.matches("""#[0-9a-f]{6}""".toRegex())

    fun byrValid(): Boolean = !byr.isNullOrBlank() && byr.toIntOrNull() in 1920..2002
    fun iyrValid(): Boolean = !iyr.isNullOrBlank() && iyr.toIntOrNull() in 2010..2020
    fun eyrValid(): Boolean = !eyr.isNullOrBlank() && eyr.toIntOrNull() in 2020..2030
    fun hgtValid(): Boolean = !hgt.isNullOrBlank() && hgt.asHeight()?.isValid() ?: false
    fun hclValid(): Boolean = !hcl.isNullOrBlank() && hcl.isColour()
    fun eclValid(): Boolean = !ecl.isNullOrBlank() && ecl in VALID_EYE_COLOURS
    fun pidValid(): Boolean = !pid.isNullOrBlank() && pid.all { it.isDigit() } && pid.length == 9

    fun passportValid(): Boolean = byrValid() && iyrValid() && eyrValid() && hgtValid() && hclValid()
            && eclValid() && pidValid()
}

fun parsePassport(lines: List<String>): Passport =
    lines.flatMap { it.split(" ") }
        .map { it.split(":")}
        .map { (label, value) -> label to value }
        .toMap().withDefault { null }
        .let { Passport(
            byr = it["byr"],
            iyr = it["iyr"],
            eyr = it["eyr"],
            hgt = it["hgt"],
            hcl = it["hcl"],
            ecl = it["ecl"],
            pid = it["pid"],
            cid = it["cid"]
        ) }

fun readInput(): List<Passport> = File(ClassLoader.getSystemResource("aoc_04_input.txt").file)
    .readLines()
    .fold(listOf(mutableListOf<String>()), { acc, raw ->
        if (raw.isBlank()) acc + listOf(mutableListOf()) else {acc[acc.lastIndex].add(raw); acc}
    }).map(::parsePassport)

fun main() {
    val passports = readInput()
    println("Loaded ${passports.size} passports")
    val numCompletePassports = passports.count { it.fieldsPresent() }
    println("There are $numCompletePassports valid passports (passports with all fields)")

    val numValidPassports = passports.count { it.passportValid() }
    println("There are $numValidPassports valid passports (passports with all fields)")
}