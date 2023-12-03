package com.hopkins.aoc.day1

import java.io.File

const val debug = false
val digitRange = '0'.rangeTo('9')

/** Advent of Code 2023: Day 1 */
fun main() {
    // Read the lines into a list
    val inputFile = File("input/input1.txt")
    val lines: List<String> = inputFile.readLines()
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

/** Returns `true` if the specified [Char] is a digit (0-9). */
fun isDigit(ch: Char): Boolean = digitRange.contains(ch)

/** Returns the calibration value from the line.  Concatenates the first and last digit in the line. */
fun extractCalibrationValue(line: String): Int {
    return line.first(::isDigit).digitToInt() * 10 +
            line.last(::isDigit).digitToInt()
}