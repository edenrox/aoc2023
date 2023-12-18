package com.hopkins.aoc.day18

import com.hopkins.aoc.day17.Point
import java.io.File

const val debug = true
const val part = 2

/** Advent of Code 2023: Day 17 */
fun main() {
    // Step 1: Read the file input
    val lines: List<String> = File("input/input18.txt").readLines()
    if (debug) {
        println("Step 1: Read file")
        println("=======")
        println("  num lines: ${lines.size}")
    }

    // Step 2: Build the map
    val map = mutableMapOf<Point, Point>()
    val start = Point.of(100, 100)
    map.put(start, Point.ORIGIN)

    var current = start
    lines.forEach { line ->
        // Parse the line
        val (dirStr, countStr, colorPart) = line.split(" ")
        val color = colorPart.substring(2, colorPart.indexOf(")"))
        val direction = directionLookup1[dirStr]!!
        val count = countStr.toInt()
        println("Color: $color Count: $count")
        for (index in 0 until count) {
            current = current.add(direction)
            map[current] = if (index == count - 1) Point.ORIGIN else direction
        }
    }

    val left = map.keys.minOf { it.x }
    val right = map.keys.maxOf { it.x }
    val top = map.keys.minOf { it.y }
    val bottom = map.keys.maxOf { it.y }

    if (debug) {
        println()
        println("Step 2: Build the map")
        println("=======")
        println("  width: ${right - left + 1}")
        println("  height: ${bottom - top + 1}")
        for (y in top..bottom) {
            print("  ")
            for (x in left..right) {
                val point = Point.of(x, y)
                val direction = map[point]
                print(
                    when (direction) {
                        directionUp, directionDown -> "|"
                        directionLeft, directionRight -> "-"
                        Point.ORIGIN -> "+"
                        else -> "."
                    }
                )
            }
            println()
        }
    }

    // Step 3: Count the area inside the map
    var area = 0L
    for (y in top .. bottom) {
        var isInside = false
        var lastCornerDirection = Point.ORIGIN
        var lineArea = 0L
        for (x in left..right) {
            val point = Point.of(x, y)
            val direction = map[point]
            if (direction != null) {
                lineArea++
                if (direction == directionUp || direction == directionDown) {
                    // Vertical piece, we're stepping in or out of the whole
                    isInside = !isInside
                } else if (direction == Point.ORIGIN) {
                    val cornerDirection =
                        if (map.containsKey(point.add(directionUp))) {
                            directionUp
                        } else {
                            directionDown
                        }
                    // Corner piece, we
                    if (lastCornerDirection == Point.ORIGIN) {
                        lastCornerDirection = cornerDirection
                    } else {
                        if (lastCornerDirection != cornerDirection) {
                            isInside = !isInside
                        }
                        lastCornerDirection = Point.ORIGIN
                    }
                }
            } else {
                if (isInside) {
                    lineArea++
                }
            }
        }
        if (debug) {
            println("Line num=$y area=$lineArea")
        }
        area += lineArea
    }
    println("Total area inside: $area") // 49061
}

val directionUp = Point.of(0, -1)
val directionDown = Point.of(0, 1)
val directionLeft = Point.of(-1, 0)
val directionRight = Point.of(1, 0)
val directions = listOf(directionUp, directionDown, directionLeft, directionRight)

val directionLookup1 = mapOf(
    "L" to directionLeft,
    "U" to directionUp,
    "R" to directionRight,
    "D" to directionDown
)

val directionLookup2 = mapOf(
    '0' to directionRight,
    '1' to directionDown,
    '2' to directionLeft,
    '3' to directionUp
)