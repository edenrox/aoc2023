package com.hopkins.aoc.day3

import java.io.File

const val debug = true
const val part = 2
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
    val partProximityMap: Map<Position, Part> =
        parts.flatMap { part -> part.position.getSurrounding().map { it to part } }.toMap()

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

    // Part 1 only: Filter to only part numbers within proximity
    val validParts: List<PartNumber> =
        partNumbers.filter { partNumber ->
            partProximityMap.keys.intersect(partNumber.getPositions()).isNotEmpty() }

    // Part 2 only: Find pairs of parts that are both near the same gear
    val partPairs: List<Pair<PartNumber, PartNumber>> =
        parts.mapNotNull { part ->
            val numberList: List<PartNumber> =
                partNumbers.filter { partNumber -> isInProximity(part, partNumber) }
            if (numberList.size == 2) {
                Pair(numberList[0], numberList[1])
            } else {
                null
            }
        }

    if (debug) {
        if (part == 1) {
            println("Valid Parts:");
            validParts.take(3).forEach { println(" $it") }
        } else {
            println("Part Pairs:")
            println("Pairs: $partPairs")
        }
    }

    if (part == 1) {
        val partNumberSum: Int = validParts.sumOf { it.toInt() }
        println("Part Number Sum: $partNumberSum") // 535078
    } else {
        val gearRatioSum: Long = partPairs.map { it.first.toInt().toLong() * it.second.toInt().toLong() }.sum()
        println("Gear ratio sum: $gearRatioSum")
    }
}

fun isInProximity(part: Part, number: PartNumber): Boolean =
     part.position.getSurrounding().intersect(number.getPositions()).isNotEmpty()

fun isPart(c: Char) =
    if (part == 1) {
        c != '.' && !digitRange.contains(c)
    } else {
        c == '*'
    }

val surroundingIntRange = IntRange(-1, 1)

data class Position(val x: Int, val y: Int) {
    override fun toString(): String = "[$x, $y]"

    fun getSurrounding(): Set<Position> =
        surroundingIntRange.flatMap { dx ->
            surroundingIntRange.map { dy ->
                Position(x + dx, y + dy)
            }}
            .toSet()
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