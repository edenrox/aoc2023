package com.hopkins.aoc.day14

import java.io.File

const val debug = true
const val part = 2

val cache = mutableMapOf<String, Int>()
val loadFactorLookup = mutableMapOf<Int, Int>()

/** Advent of Code 2023: Day 14 */
fun main() {
    // Read the file input
    val lines: List<String> = File("input/input14.txt").readLines()
    val numLines = lines.size
    if (debug) {
        println("Num lines: $numLines")

        println("Input")
        println("=====")
        lines.take(10).forEach { println(it) }
    }

    // Calculate the lines after tilting
    val output = lines.map { it.toCharArray().toMutableList() }.toMutableList()
    val directionList =
        if (part == 1) {
            listOf(Direction.RIGHT)
        } else {
            listOf(Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT)
        }
    val numCycles = if (part == 1) 1 else 10000
    var cycleStart = 0
    var cycleEnd = 0
    for (cycle in 0 until numCycles) {
        val key = buildCacheKey(output)
        if (cache.containsKey(key)) {
            println("Cycle found cycle=$cycle orig=${cache[key]!!}")
            cycleStart = cache[key]!!
            cycleEnd = cycle
            break;
        }
        cache[key] = cycle
        for (direction in directionList) {
            calculateAfterTilt(output, direction)
        }
        loadFactorLookup[cycle] = calculateLoadFactor(output)
    }

    val afterTilt = output.map { it.joinToString(separator = "") }.toList()
    if (debug) {
        println("After Tilt")
        println("==========")
        afterTilt.take(10).forEach { println(it) }
    }

    if (part == 1) {
        val totalLoad = loadFactorLookup[0]
        println("Total Load: $totalLoad") // 106648
    } else {
        val cycleLength = cycleEnd - cycleStart
        val target = 1_000_000_000
        val numCycles = (target - cycleStart) / cycleLength
        val offset = target - (numCycles * cycleLength) - 1

        if (debug) {
            println("Cycle start=$cycleStart end=$cycleEnd length=$cycleLength")
            println("Target target=$target numCycles=$numCycles offset=$offset")
        }

        val totalLoad = loadFactorLookup[offset]
        println("Total Load: $totalLoad")
    }
}

fun calculateLoadFactor(data: MutableList<MutableList<Char>>): Int {
    val numLines = data.size
    return data.mapIndexed { index, line ->
        val factor = numLines - index
        countRoundRocks(line) * factor
    }.sum()
}
fun buildCacheKey(data: MutableList<MutableList<Char>>): String =
    data.joinToString(separator = "\n") { it.joinToString(separator = "") }

fun calculateAfterTilt(data: MutableList<MutableList<Char>>, direction: Direction) {
    val width = data[0].size
    val height = data.size

    val ybounds = 0 until height
    val xbounds = 0 until width

    val yrange = if (direction == Direction.UP) 0 until height else height-1 downTo 0
    val xrange = if (direction == Direction.LEFT) 0 until width else width-1 downTo 0
    yrange.forEach { y ->
        xrange.forEach { x ->
            val c = data[y][x]
            if (c == '.') {
                var dy = -direction.delta.y
                var dx = -direction.delta.x
                while (y+dy in ybounds &&
                    x+dx in xbounds &&
                    data[y+dy][x+dx] == '.') {
                    dy -= direction.delta.y
                    dx -= direction.delta.x
                }
                if (y+dy in ybounds &&
                    x+dx in xbounds &&
                    data[y+dy][x+dx] == 'O') {
                    data[y][x] = 'O'
                    data[y+dy][x+dx] = '.'
                }
            }
        }
    }
}

fun countRoundRocks(chars: List<Char>): Int {
    return chars.count { it == 'O' }
}

fun countRoundRocks(line: String): Int {
    return line.count { it == 'O' }
}

enum class Direction(val delta: Point) {
    UP(Point(0, -1)),
    DOWN(Point(0, 1)),
    LEFT(Point(-1, 0)),
    RIGHT(Point(1, 0)),
}

class Point(val x: Int, val y: Int)