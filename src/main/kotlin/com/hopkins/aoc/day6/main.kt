package com.hopkins.aoc.day6

import java.io.File

const val debug = true
const val part = 2

/** Advent of Code 2023: Day 6 */
fun main() {
    val inputFile = File("input/input6.txt")
    val lines: List<String> = inputFile.readLines()

    val times = readValues(lines[0])
    val distances = readValues(lines[1])

    if (debug) {
        println("Times: $times")
        println("Distances: $distances")
    }

    val ways: List<Int> =
        times.zip(distances)
            .map { (time, distance) -> findWays(time, distance) }
    if (debug) {
        println("Ways: $ways")
    }
    val result: Int =
        ways.fold(1) { acc, item -> acc * item }
    print("Result: $result") // 588588
}

/**
 * Returns the number of ways in which the boat can travel the distance.
 *
 * Start with holding the button for 1 second, then 2 seconds, etc.  then calculate
 * the distance travelled in the time remaining.
 */
fun findWays(totalTime: Long, distance: Long): Int {
    val range = LongRange(1, totalTime - 1)
    val start = range.first { calculateDistanceTravelled(it, totalTime) > distance }
    val end = range.last { calculateDistanceTravelled(it, totalTime) > distance }
    return LongRange(start, end).count()
}

/** Calculate the distance traveled given the time held and total time. */
fun calculateDistanceTravelled(timeHeld: Long, totalTime: Long): Long {
    // Speed = 1 mm/s for each second we hold the button
    val speed = timeHeld

    // Distance (mm) = Speed (mm/s) * Time remaining (s)
    return speed * (totalTime - timeHeld)
}

/** Returns list of integers after the ":" separated by 1 or more spaces. */
fun readValues(line: String): List<Long> {
    val (_, values) = line.split(":")
    if (part == 1) {
        return values.split(" ").filterNot { it.trim().isBlank() }.map { it.toLong() }
    } else {
        return listOf(values.split(" ").filterNot { it.trim().isBlank() }.joinToString(separator = "").toLong())
    }
}