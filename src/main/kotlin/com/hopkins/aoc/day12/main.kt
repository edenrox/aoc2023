package com.hopkins.aoc.day12

import java.io.File

const val debug = true
const val part = 2

/** Advent of Code 2023: Day 12 */
fun main() {
    val lines: List<String> = File("input/input12-example.txt").readLines()

    val springLookup: Map<Char, SpringType> = SpringType.values().associateBy { it.symbol }

    val sum = lines.sumOf { line ->
        val max = 5
        var (left, right) = line.split(" ")
        if (part == 2) {
            left = (0 until max).joinToString(separator = "?") { left }
            right = (0 until max).joinToString(separator = ",") { right }
        }
        val types = left.map { c -> springLookup[c]!! }
        val counts = right.split(",").map { it.toInt() }
        val numUnknowns = types.count { it == SpringType.UNKNOWN }
        val numPossibilities = Math.pow(2.0, numUnknowns.toDouble()).toLong()

        if (debug) {
            printTypes(types)
            println("Counts: $counts")
            println("Possibilities: $numPossibilities")
        }

        // Generate all possible sequences
        val possibilities: List<List<SpringType>> =
            //emptyList()
            if (types.last() == SpringType.UNKNOWN) {
                generatePossibilities(types.reversed(), counts.reversed())
            } else {
                generatePossibilities(types, counts)
            }
        println("Generated: ${possibilities.size}")
        //require(numPossibilities == possibilities.size)

        // Only count valid possibilities
        var validPossibilities = possibilities.count { isValid(it, counts) }
        println("Valid: $validPossibilities")
        validPossibilities
    }
    println("Sum: $sum")
}

fun printTypes(types: List<SpringType>) {
    println(types.map { it.symbol }.joinToString(prefix = "[", postfix = "]"))
}

fun generatePossibilities(types: List<SpringType>, counts: List<Int>): List<List<SpringType>> {
    val unknownPositions: List<Int> =
        types.indices.filter { types[it] == SpringType.UNKNOWN }
    val output = mutableListOf(emptyList<SpringType>())
    val totalDamaged = counts.sum()
    println("|".repeat(unknownPositions.size))
    unknownPositions.forEach { position ->
        print(".")
        val numPossiblyDamaged = types.subList(position, types.size - 1).count { type -> type == SpringType.DAMAGED || type == SpringType.UNKNOWN}
        val segment = types.subList(output[0].size, position)
        val newOutput = output.flatMap {
            listOf(
                it + segment + listOf(SpringType.OPERATIONAL),
                it + segment + listOf(SpringType.DAMAGED))
        }.filter {
            val numDamaged = it.count { type -> type == SpringType.DAMAGED}
            val checkCounts = combineCounts(0, it)
            val prefix = checkCounts.dropLast(1)
            var isValid = false
            if (numDamaged + numPossiblyDamaged < totalDamaged) {
                isValid = false
            } else if (checkCounts.size > counts.size) {
                isValid = false
            } else if (checkCounts == counts.take(checkCounts.size)) {
                isValid = true
            } else if (prefix == counts.take(prefix.size) &&
                (checkCounts.last() <= counts[prefix.size])) {
                isValid = true
            }
            if (!isValid) {
                //println("Filter start=$it checkCounts=$checkCounts prefix=$prefix")
            }
            isValid
        }
        if (newOutput.isEmpty()) {
            println("Output is empty")
        }
        output.clear()
        output.addAll(newOutput)
    }
    println()
    val endPositions = types.size - 1 - unknownPositions.last()
    if (endPositions > 0) {
        val newOutput = output.map { it + types.takeLast(endPositions) }
        output.clear()
        output.addAll(newOutput)
    }
    //println("Output: ${output.joinToString(separator = "\n")}")
    return output
}


fun isValid(types: List<SpringType>, counts: List<Int>): Boolean {
    val typeCounts: List<Int> = combineCounts(0, types)
    return typeCounts == counts
}

fun combineCounts(acc: Int, remaining: List<SpringType>): List<Int> {
    if (remaining.isEmpty()) {
        return if (acc == 0) {
            emptyList()
        } else {
            listOf(acc)
        }
    }
    return when (remaining.first()) {
        SpringType.OPERATIONAL ->
            if (acc == 0) {
                combineCounts(0, remaining.drop(1))
            } else {
                listOf(acc) + combineCounts(0, remaining.drop(1))
            }
        SpringType.DAMAGED -> combineCounts(acc + 1, remaining.drop(1))
        SpringType.UNKNOWN -> throw IllegalArgumentException("remaining contains UNKNOWN")
    }
}

enum class SpringType(val symbol: Char) {
    OPERATIONAL('.'),
    DAMAGED('#'),
    UNKNOWN('?'),
}