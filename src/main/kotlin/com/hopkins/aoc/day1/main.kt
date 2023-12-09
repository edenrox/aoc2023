package com.hopkins.aoc.day1

import java.io.File

const val debug = true
const val part = 2
val digits = listOf("1", "2", "3", "4", "5", "6" ,"7", "8", "9")
val digitNames = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
val valueMap: Map<String, Int> = buildMap {
    digits.mapIndexed { index, digit -> put(digit, index + 1)}
    digitNames.mapIndexed { index, digit -> put(digit, index + 1)}
}

/** Advent of Code 2023: Day 1 */
fun main() {
    // Read the lines into a list
    val inputFile = File("input/input1.txt")
    val lines: List<String> = inputFile.readLines()
    if (debug) {
        println("Value map: $valueMap")
    }

    if (debug) {
        println("Value | Line")
        lines.take(5).forEach { line ->
            val value = extractCalibrationValue(line)
            println("$value | $line")
        }
    }

    // Calculate the sum of calibration values extracted from each line
    val output = lines.sumOf { extractCalibrationValue(it) }
    println("Output: $output") // 54990
}

/** Returns the calibration value from the line.  Concatenates the first and last digit in the line. */
fun extractCalibrationValue(line: String): Int {
    return findDigit(line, true) * 10 + findDigit(line, false)
}

fun findDigit(line: String, isFirst: Boolean): Int {
    val list = buildList {
        add(digits)
        if (part == 2) {
            add(digitNames)
        }
    }
    val firstDigit = list.flatMap { container ->
        container
            .filter { digit -> line.contains(digit) }
            .map { digit -> Pair(digit, if (isFirst) { line.indexOf(digit) } else { line.lastIndexOf(digit)})}
    }.minBy { if (isFirst) { it.second } else { line.length - it.second } }.first
    return valueMap[firstDigit]!!
}