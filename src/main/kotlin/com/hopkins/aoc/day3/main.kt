package com.hopkins.aoc.day3

import java.io.File

const val debug = false
val digitRange = '0'.rangeTo('9')

/** Advent of Code 2023: Day 3 */
fun main() {
    val inputFile = File("input/input3.txt")
    val lines: List<String> = inputFile.readLines()

    if (debug) {
        // Print the lines
        println("Lines:")
        lines.take(3).forEachIndexed { y, line -> println(" y: $y, line: $line") }
    }

    // Find the parts
    val parts: List<Part> =
        lines.flatMapIndexed { y, line ->
            line.mapIndexed { x, c ->
                if (isPart(c)) {
                    Part(c, Position(x, y))
                } else {
                    null
                }
            }
        }.filterNotNull()

    // Build a map of positions which are within proximity of a part
    val partProximitySet: Set<Position> =
        parts.flatMap { it.position.getSurrounding() }.toSet()

    if (debug) {
        // Print the parts (to debug)
        println("Parts: ")
        parts.take(3).forEach { println(" $it") }

        // Print the part proximity (to debug)
        println("Part Proximity: ")
        parts.take(3).forEach { it.position.getSurrounding().forEach { println(" $it") } }
    }

    // Find all the part numbers
    val numberPattern = Regex("[0-9]+")
    val partNumbers: List<PartNumber> =
        lines.flatMapIndexed { y, line ->
            numberPattern.findAll(line).map { matchResult ->
                PartNumber(matchResult.value, Position(matchResult.range.first, y))
            }
        }

    // Filter to only part numbers within proximity
    val validParts: List<PartNumber> =
        partNumbers.filter { partProximitySet.intersect(it.getPositions()).isNotEmpty() }

    if (debug) {
        println("Valid Parts:");
        validParts.take(3).forEach { println(" $it") }
    }

    val partNumberSum: Int = validParts.sumOf { it.toInt() }
    println("Part Number Sum: $partNumberSum") // 535078
}

fun isPart(c: Char) = c != '.' && !digitRange.contains(c)

val surroundingIntRange = IntRange(-1, 1)

data class Position(val x: Int, val y: Int) {
    override fun toString(): String = "[$x, $y]"

    fun getSurrounding(): List<Position> =
        surroundingIntRange.flatMap { dx ->
            surroundingIntRange.map { dy ->
                Position(x + dx, y + dy)
            }}
}

class Part(val symbol: Char, val position: Position) {
    override fun toString(): String = "Part {c=$symbol pos=$position}"
}

class PartNumber(val symbol: String, val position: Position) {
    fun toInt(): Int = symbol.toInt()

    fun getPositions(): List<Position> =
        IntRange(position.x, position.x + symbol.length - 1).map { tx -> Position(tx, position.y)}

    override fun toString(): String = "PartNumber {$symbol pos=$position}"
}