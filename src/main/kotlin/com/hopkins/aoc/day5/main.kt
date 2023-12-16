package com.hopkins.aoc.day5

import java.io.File

const val debug = true
const val part = 2

/** Advent of Code 2023: Day 5 */
fun main() {
    // Read the input
    val lines: List<String> = File("input/input5.txt").readLines()

    // Step 1: read the seeds
    val (_, seedsPart) = lines[0].split(": ")
    val seedNumbers = seedsPart.split(" ").map { it.toLong() }
    val seedRanges = if (part == 1) {
        seedNumbers.map { LongRange(it, it) }
    } else {
        seedNumbers.zipWithNext{ a, b -> LongRange(a, a + b - 1)}.filterIndexed { index, _ -> index % 2 == 0}
    }
    if (debug) {
        println("Seed Ranges:")
        println(seedRanges)
    }

    // Step 2: read the Range increments
    val ranges =
        lines.drop(1).filter { it.isNotBlank() }.fold(mutableListOf<MutableList<RangeIncrement>>()) {
                acc, line ->
            if (line.endsWith("map:")) {
                acc.add(mutableListOf<RangeIncrement>())
            } else {
                acc.last().add(parseRangeIncrement(line))
            }
            acc
        }

    // Step 3: map the seeds through the ranges
    var current: List<LongRange> = seedRanges
    for (rangeList in ranges) {
        val splits: List<Long> = rangeList.flatMap { listOf(it.range.first, it.range.last)}.sorted().distinct()
        val splitRanges = current.flatMap { inputRange -> splitRange(inputRange, splits) }
        if (debug) {
            println("Range List: $rangeList")
            println("Splits: $splits")
            println("Split Ranges: $splitRanges")
        }

        current = splitRanges.map { inputRange ->
            var output = inputRange
            for (rangeIncrement in rangeList) {
                if (rangeIncrement.contains(inputRange)) {
                    output = rangeIncrement.convert(inputRange)
                    break
                }
            }
            output
        }
    }
    val minLocation = current.minOf { it.first }
    println("Minimum location: $minLocation")

    // Part 1: 621354867
    // Part 2:
}

fun parseRangeIncrement(line: String): RangeIncrement {
    val (dest, src,length) = line.split(" ").map { it.toLong() }
    return RangeIncrement(LongRange(src, src + length - 1), dest - src)
}

data class RangeIncrement(val range: LongRange, val increment: Long) {

    fun contains(inputRange: LongRange) = range.contains(inputRange.first)

    fun convert(inputRange: LongRange) = LongRange(inputRange.first + increment, inputRange.last + increment)
}

fun splitRange(range: LongRange, splits: List<Long>): List<LongRange> {
    return if (splits.isEmpty()) {
        listOf(range)
    } else if (range.contains(splits.first())) {
        val (left, right) = splitRange(range, splits.first())
        listOf(left) + splitRange(right, splits.drop(1))
    } else {
        splitRange(range, splits.drop(1))
    }
}

fun splitRange(range: LongRange, split: Long): List<LongRange> {
    require(range.contains(split))
    return listOf(LongRange(range.start, split - 1), LongRange(split, range.last))
}