package com.hopkins.aoc.day16

import java.io.File
import java.lang.IllegalStateException

const val debug = false
const val part = 2

val history = mutableSetOf<Beam>()

/** Advent of Code 2023: Day 16 */
fun main() {
    // Read the file input
    val lines: List<String> = File("input/input16.txt").readLines()
    val numLines = lines.size
    if (debug) {
        println("Num lines: $numLines")
    }

    val map: Map<Point, Char> =
        lines.flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                if (c == '.') {
                    null
                } else {
                    Point(x, y) to c
                }
            }
        }.toMap()
    val mapWidth = lines[0].length
    val mapHeight = lines.size

    if (debug) {
        println("Map")
        println("===")
        for (y in 0 until mapWidth) {
            for (x in 0 until mapHeight) {
                val c = map.getOrDefault(Point(x, y), '.')
                print(c)
            }
            println()
        }
    }

    val startList: List<Beam> =
        if (part == 1) {
            listOf(Beam(Point(0, 0), directionRight))
        } else {
            (0 until mapWidth).flatMap {
                listOf(
                    Beam(Point(it, 0), directionDown),
                    Beam(Point(it, mapHeight-1), directionUp))
            } +
            (0 until mapHeight).flatMap {
                listOf(
                    Beam(Point(0, it), directionRight),
                    Beam(Point(mapWidth - 1, it), directionLeft))
            }
        }

    val maxEnergy =
        startList.maxOf { start ->
            history.clear()
            val current = mutableListOf(start)
            while (current.isNotEmpty()) {
                val beam = current.removeFirst()
                history.add(beam)

                val next = beam.nextBeams(map)
                    .filterNot { history.contains(it) }
                    .filterNot { isOutsideBounds(it.position, mapWidth, mapHeight) }
                current.addAll(next)
            }

            val energized =
                history.map { it.position }.toSet()

            if (debug) {
                println("Energized")
                println("=========")
                for (y in 0 until mapWidth) {
                    for (x in 0 until mapHeight) {
                        val c = if (energized.contains(Point(x, y))) '#' else '.'
                        print(c)
                    }
                    println()
                }
            }
            energized.size
        }
    print("Max Energy: $maxEnergy")
    // Part 1: 6514
    // Part 2: 8089
}

fun isOutsideBounds(position: Point, width: Int, height: Int) =
    position.x < 0 || position.y < 0 || position.x >= width || position.y >= height

data class Beam(val position: Point, val direction: Point) {

    private fun newBeams(vararg directions: Point): List<Beam> {
        return directions.map { Beam(position.add(it), it) }
    }

    fun nextBeams(map: Map<Point, Char>): List<Beam> {
        val tile: Char = map.getOrDefault(position, '.')
        return when (tile) {
            '.' -> newBeams(direction)
            '|' -> if (direction in verticalDirections) {
                newBeams(direction)
            } else {
                newBeams(directionUp, directionDown)
            }
            '-' -> if (direction in verticalDirections) {
                newBeams(directionLeft, directionRight)
            } else {
                newBeams(direction)
            }
            '\\', '/' -> newBeams(getNextDirection(tile))
            else -> throw IllegalStateException("Unexpected tile: $tile at $position")
        }
    }

    private fun getNextDirection(cornerTile: Char): Point {
        require(cornerTile in "\\/")
        return when (direction) {
            directionUp -> if (cornerTile == '/') directionRight else directionLeft
            directionDown -> if (cornerTile == '/') directionLeft else directionRight
            directionLeft -> if (cornerTile == '/') directionDown else directionUp
            directionRight -> if (cornerTile == '/') directionUp else directionDown
            else -> throw IllegalStateException("Unexpected direction: $direction")
        }
    }
}

val directionUp = Point(0, -1)
val directionLeft = Point(-1, 0)
val directionRight = Point(1, 0)
val directionDown = Point(0 , 1)

val verticalDirections = setOf(directionUp, directionDown)

data class Point(val x: Int, val y: Int) {

    fun add(dx: Int, dy: Int): Point =
        Point(x + dx, y + dy)

    fun add(other: Point) = add(other.x, other.y)
}