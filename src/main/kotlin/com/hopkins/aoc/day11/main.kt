package com.hopkins.aoc.day11

import java.io.File
import kotlin.math.abs

const val debug = true
const val part = 2

/** Advent of Code 2023: Day 11 */
fun main() {
    val lines: List<String> = File("input/input11.txt").readLines()

    // Read the galaxies from file
    var i = 0
    val inputGalaxies: Set<Galaxy> =
        lines.flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                if (c == '#') {
                    Galaxy(i++, Point(x, y))
                } else {
                    null
                }
            }}.toSet()
    if (debug) {
        println("Num Galaxies: ${inputGalaxies.count()}")
        println("Input Galaxies: ${inputGalaxies.take(5)}")
    }

    // Find the empty rows/columns
    val max = 140
    val allList = (0..max).toList()
    val emptyRows = allList.toMutableSet()
    val emptyColumns = allList.toMutableSet()
    inputGalaxies.forEach { galaxy ->
        emptyRows.remove(galaxy.position.y)
        emptyColumns.remove(galaxy.position.x)
    }
    if (debug) {
        println("Empty rows: $emptyRows")
        println("Empty columns: $emptyColumns")
    }

    // Calculate the actual galaxy positions
    val factor = if (part == 1) { 2 } else { 1_000_000 }
    val actualGalaxies =
        inputGalaxies.map { input ->
            val dx = emptyColumns.count { it < input.position.x } * (factor - 1)
            val dy = emptyRows.count { it < input.position.y } * (factor - 1)
            Galaxy(input.id, input.position.add(dx, dy))
        }
    if (debug) {
        println("Actual Galaxies: ${actualGalaxies.take(5)}")
    }


    // Find the pairs of galaxies
    val allPairs = actualGalaxies.flatMapIndexed { index, g1 ->
        actualGalaxies.drop(index + 1).map { g2 -> g1 to g2 }}
    if (debug) {
        println("Num pairs: ${allPairs.count()}")
    }

    // Find the distance between pairs of points
    val distanceSum = allPairs.sumOf {
        it.first.position.walkDistance(it.second.position).toLong()
    }
    println("Distance Sum: $distanceSum") // 857986849428
}

data class Galaxy(val id: Int, val position: Point)

data class Point(val x: Int, val y: Int) {

    fun add(dx: Int, dy: Int) = Point(x + dx, y + dy)

    fun add(other: Point): Point = add(other.x, other.y)

    fun walkDistance(other: Point): Int =
        abs(x - other.x) + abs(y - other.y)
}

