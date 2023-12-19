package com.hopkins.aoc.day18

import com.hopkins.aoc.day17.Point
import java.io.File

const val debug = true
const val part = 2

/** Advent of Code 2023: Day 18 */
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
    map[start] = Point.ORIGIN

    var current = start
    var areaAlongPath = 0L
    val foundCorners = mutableSetOf<Point>(start)
    val lineCounts = mutableMapOf<Int, Int>()
    lines.forEach { line ->
        // Parse the line
        val (dirStr, countStr, colorPart) = line.split(" ")
        val color = colorPart.substring(2, colorPart.indexOf(")"))
        val direction =
            if (part == 1) {
                directionLookup1[dirStr]!!
            } else {
                directionLookup2[color.last()]!!
            }
        val count =
            if (part == 1) {
                countStr.toInt()
            } else {
                color.substring(0, 5).toInt(radix = 16)
            }
        areaAlongPath += count
        if (direction == directionUp) {

        }
    }
    print("Area Along Path=$areaAlongPath")
    if (true) {
        return
    }

    val left = map.keys.minOf { it.x }
    val right = map.keys.maxOf { it.x }
    val top = map.keys.minOf { it.y }
    val bottom = map.keys.maxOf { it.y }
    val width = right - left + 1
    val height = bottom - top + 1

    if (debug) {
        println()
        println("Step 2: Build the map")
        println("=======")
        println("  width: $width")
        println("  height: $height")
        if (width < 11) {
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
    }

    // Step 3: Count the area inside the map
    val corners = map.filter {(key, value) -> value == Point.ORIGIN }
    println("Num corners: ${corners.size}")

    val cornersByRow = corners.map { it.key.y to it.key.x }.groupBy({ it.first }, {it.second })
    val importantCorners = mutableSetOf<Point>()
    for (row in cornersByRow.keys) {
        val cornersInRow = cornersByRow[row]!!.sorted()
        println("row=$row corners=$cornersInRow")
        require(cornersInRow.size % 2 == 0)
        for (index in 0 until cornersInRow.size / 2) {
            val first = cornersInRow[index * 2]
            val c1 = Point.of(first, row)
            val second = cornersInRow[index * 2 + 1]
            val c2 = Point.of(second, row)
            val t1 = map[c1.add(directionUp)] == null
            val t2 = map[c2.add(directionUp)] == null
            if (t1 != t2) {
                importantCorners.add(if (index % 2 == 0) c2 else c1)
            }
        }
    }
    val importantCornersByRow = importantCorners.map { it.y to it.x }.groupBy({ it.first }, {it.second })
    println("Num important corners: ${importantCorners}")

    val verticalByRow = map
        .filter { it.value == directionUp || it.value == directionDown }
        .entries
        .groupBy( {it.key.y}, {it.key.x})
    var areaInPath = 0L
    for (row in verticalByRow.keys) {
        val rowValues = (verticalByRow[row]!! + (importantCornersByRow[row] ?: emptyList())).sorted()
        if (rowValues.size % 2 != 0) {
            println("row=$row values=$rowValues")
            println("Hi")
        }
        for (index in 0 until rowValues.size / 2) {
            val first = rowValues[index * 2]
            val second = rowValues[index * 2 + 1]
            areaInPath += (second - first - 1)
        }
    }


    var area = areaAlongPath - 1 + areaInPath
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