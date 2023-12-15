package com.hopkins.aoc.day12

import java.io.File

const val debug = true
const val part = 2

/** Advent of Code 2023: Day 12 */
fun main() {
    val lines: List<String> = File("input/input12.txt").readLines()

    val lineInputs: List<LineInput> =
        lines.mapIndexed { index, line ->
            val max = if (part == 2) 5 else 1
            var (left, right) = line.split(" ")
            left = (0 until max).joinToString(separator = "?") { left }
            right = (0 until max).joinToString(separator = ",") { right }
            LineInput(index + 1, left, right)
        }

    val sum: Long =
        lineInputs.sumOf {
            val numWays = countNumWaysCached(it.groups, it.counts)
            println("Line Input: $it")
            println("Num ways: $numWays")
            numWays
        }
    println("Sum: $sum")
}

val cache = mutableMapOf<CacheKey, Long>()

fun countNumWaysCached(groups: List<String>, counts: List<Int>): Long {
    val key = CacheKey(groups, counts)
    if (cache.containsKey(key)) {
        return cache[key]!!
    }
    val result = countNumWays(key.groups, key.counts)
    cache[key] = result
    if (debug) {
        if (counts.size < 2) {
            println("key=$key, result=$result")
        }
    }
    return result
}

fun countNumWays(groups: List<String>, counts: List<Int>): Long {
    if (groups.isEmpty() || (groups.size == 1 && groups.first() == "")) {
        // Base cases: groups is empty
        return if (counts.isEmpty()) 1 else 0
    }

    if (groups.size == 1) {
        val firstGroup = groups[0]
        if (firstGroup.length == 1) {
            // Base case: groups has 1 element and it is 1 character
            if (firstGroup == "#") {
                return if (counts.size == 1 && counts.first() == 1) {
                    1
                } else {
                    0
                }
            } else {
                require(firstGroup == "?")
                return if (counts.isEmpty()) {
                    1
                } else if (counts.size == 1 && counts.first() == 1) {
                    1
                } else {
                    0
                }
            }
        } else if (!firstGroup.contains("?")) {
            // Base case: group contains only "#"
            return if (counts.size == 1 && counts.first() == firstGroup.length) 1 else 0
        } else if (counts.isEmpty()) {
            // Base case: there are no counts, so there can be no more operational springs
            return if (firstGroup.contains("#")) {
                0
            } else {
                1
            }
        } else {
            require(counts.isNotEmpty())

            // Recursive case: group has 1 element and it is 2+ characters with 1 or more '?'s
            val partitionPositions = firstGroup.indices.filter { firstGroup[it] == '?'}
            var ways = 0L

            // Option 1: we don't partition the group, thus it is all #
            if (counts.size == 1 && counts.first() == firstGroup.length) {
                return 1
            }
            // Option 2: we partition the group at one of the question marks
            for (position in partitionPositions) {
                val leftGroup = listOf(firstGroup.take(position).replace("?", "#"))
                val rightGroup = listOf(firstGroup.drop(position + 1))
                //println("firstGroup=$firstGroup partitionPos=$position left=$leftGroup right=$rightGroup")
                val leftWays = countNumWaysCached(leftGroup, emptyList()) * countNumWaysCached(rightGroup, counts)
                val rightWays = countNumWaysCached(leftGroup, counts.take(1)) * countNumWaysCached(rightGroup, counts.drop(1))
                ways += leftWays + rightWays

                //if (firstGroup == "????" && ways > 0) {
                    println("O2: group=$firstGroup left=${leftGroup[0]} right=${rightGroup[0]} counts=$counts leftWay=$leftWays rightWays=$rightWays")
                //}
            }
            return ways
        }
    } else {
        var ways = 0L
        for (i in 0..counts.size) {
            val leftWays = countNumWaysCached(groups.take(1), counts.take(i))
            val rightWays = countNumWaysCached(groups.drop(1), counts.drop(i))
            ways += leftWays * rightWays
        }
        return ways
    }
}

data class CacheKey(val groups: List<String>, val counts: List<Int>)

data class LineInput(val index: Int, val left: String, val right: String) {
    val groups = left.split(".").filter { it.isNotBlank() }
    val counts = right.split(",").map { it.toInt() }
}