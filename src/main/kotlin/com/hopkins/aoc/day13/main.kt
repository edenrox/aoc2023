package com.hopkins.aoc.day13

import java.io.File

const val debug = false
const val part = 2

/** Advent of Code 2023: Day 13 */
fun main() {
    // Read the file input
    val lines: List<String> = File("input/input13.txt").readLines()
    if (debug) {
        println("Num lines: ${lines.size}")
    }

    // Parse lines into puzzles
    val puzzles: List<Puzzle> =
        lines.fold (mutableListOf(mutableListOf<String>())) { acc, line ->
            if (line.isEmpty()) {
                acc.add(mutableListOf())
            } else {
                acc.last().add(line)
            }
            acc
        }.map { lines -> Puzzle(lines) }
    if (debug) {
        println("Num puzzles: ${puzzles.size}")
    }

    val puzzleSum = puzzles.sumOf { puzzle ->
        if (debug) {
            println("Puzzle width=${puzzle.width} height=${puzzle.height}")
            println(" firstRow=${puzzle.rows[0]} firstColumn=${puzzle.columns[0]}")
        }
        val reflectionValue = puzzle.findReflectionValue()
        println("Value: $reflectionValue")
        reflectionValue
    }

    println("Sum: $puzzleSum")
}

class Puzzle(val rows: List<String>) {
    val columns = rows[0].indices.map { index ->
        rows.map { row -> row[index] }.joinToString(separator = "")
    }
    val width = rows[0].length
    val height = rows.size

    fun findReflectionValue(): Int {
        if (debug) {
            println("Vertical:")
        }
        val verticalReflection = findReflection(height, rows)
        if (debug) {
            println("Horizontal:")
        }
        val horizontalReflection = findReflection(width, columns)

        println("VR: $verticalReflection HR: $horizontalReflection")
        return verticalReflection * 100 + horizontalReflection
    }

    private fun findReflection(max: Int, lines: List<String>): Int {
        val desiredDiff = if (part == 1) { 0 } else { 1 }
        for (index in 1 until max) {
            val num = index.coerceAtMost(max - index)
            val left = lines.subList(index - num, index)
            val right = lines.subList(index, index + num).reversed()
            if (debug) {
                println("left: $left, right: $right")
            }

            require(left.size == right.size)
            if (calcDifference(left, right) == desiredDiff) {
                return index
            }
        }
        return 0
    }
}

fun calcDifference(left: List<String>, right: List<String>): Int {
    val leftChars = left.flatMap { line -> line.toList() }
    val rightChars = right.flatMap { line -> line.toList() }
    return leftChars.zip(rightChars).sumOf { (a, b) -> if (a == b) { 0.toInt() } else { 1 } }
}