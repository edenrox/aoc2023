package com.hopkins.aoc.day6

import java.io.File

const val debug = true

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
    print("Result: $result")
}

fun findWays(totalTime: Int, distance: Int): Int {
    return IntRange(1, totalTime - 1).count { calculateDistanceTravelled(it, totalTime) > distance }
}

fun calculateDistanceTravelled(timeHeld: Int, totalTime: Int): Int {
    val speed = timeHeld
    return speed * (totalTime - timeHeld)
}

fun readValues(line: String): List<Int> {
    val (_, values) = line.split(":")
    return values.trim().split(" ").filterNot { it.trim().isBlank() }.map { it.toInt() }
}