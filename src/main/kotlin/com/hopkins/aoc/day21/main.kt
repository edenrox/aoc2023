package com.hopkins.aoc.day21

import com.hopkins.aoc.day17.Point
import java.io.File

const val debug = true
const val part = 1

/** Advent of Code 2023: Day 21 */
fun main() {
    // Step 1: Read the file input
    val lines: List<String> = File("input/input21-ex1.txt").readLines()
    if (debug) {
        println("Step 1: Read file")
        println("=======")
        println("  num lines: ${lines.size}")
    }

    // Step 2: Build the map
    var startPoint: Point = Point.of(-1, -1)
    val mapHeight = lines.size
    val mapWidth = lines[0].length
    val mapSize = Point.of(mapWidth, mapHeight)
    val rocks: Set<Point> = lines.flatMapIndexed {y, line ->
        line.mapIndexedNotNull { x, c ->
            if (c == '#') {
                Point.of(x, y)
            } else {
                if (c == 'S') {
                    startPoint = Point.of(x, y)
                }
                null
            }
        }
    }.toSet()
    require(startPoint.x >= 0)
    require(startPoint.y >= 0)

    if (debug) {
        println("Step 2: Build the map")
        println("=======")
        println("  map size: $mapSize")
        println("  start: $startPoint")
        println("  num rocks: ${rocks.size}")
    }

    var current = setOf(startPoint)
    for (i in 1..50) {
        val next =
            current.flatMap { point ->
                directions.map { direction ->
                    point.add(direction) }}
                .map { point ->
                    if (point.x == -1) {
                        Point.of(point.x + mapWidth, point.y)
                    } else if (point.y == -1) {
                        Point.of(point.x, point.y + mapWidth)
                    } else if (point.x == mapWidth) {
                        Point.of(0, point.y)
                    } else if (point.y == mapHeight) {
                        Point.of(point.x, 0)
                    } else {
                        point
                    }
                }
                .filterNot { point -> rocks.contains(point) }
                .toSet()
        println("Step $i:")
        printMap(mapSize, rocks, next)
        current = next
    }
    println("Num Plots: ${current.size}")
}

fun printMap(mapSize: Point, rocks: Set<Point>, current: Set<Point>) {
    for (y in 0 until mapSize.y) {
        for (x in 0 until mapSize.x) {
            val point = Point.of(x, y)
            if (rocks.contains(point)) {
                print("#")
            } else if (current.contains(point)) {
                print("O")
            } else {
                print(".")
            }
        }
        println()
    }
}

val directions = listOf(
    Point.of(-1, 0),
    Point.of(1, 0),
    Point.of(0, -1),
    Point.of(0, 1)
)
