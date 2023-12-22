package com.hopkins.aoc.day22

import java.io.File
import java.lang.IllegalStateException

const val debug = true
const val part = 1

/** Advent of Code 2023: Day 22 */
fun main() {
    // Step 1: Read the file input
    val lines: List<String> = File("input/input22.txt").readLines()
    if (debug) {
        println("Step 1: Read file")
        println("=======")
        println("  num lines: ${lines.size}")
    }

    // Step 2: Parse the input into cubes
    val cubes: List<Cube> = lines.mapIndexed { index, line -> parseCube(index, line) }
    if (debug) {
        println("Step 2: Parse cubes")
        println("=======")
        cubes.forEach { cube -> println("  $cube") }
    }

    // Step 3: Sort the cubes by descending z position
    val sortedCubes = cubes.sortedBy { it.origin.z }

    // Step 4: Drop the cubes to the bottom
    var droppedCubes = mutableListOf<Cube>()
    for (cube in sortedCubes) {
        val droppedCube = dropCube(cube, droppedCubes)
        droppedCubes.add(droppedCube)
    }
    if (debug) {
        println("Step 5: Drop cubes")
        println("=======")
        //printCubeStack(droppedCubes.toList())
    }

    // Step 5: Find the disintegratable cubes
    val sortedAfterDrop = droppedCubes.sortedBy { it.origin.z }
    val cubeMap = droppedCubes.flatMap { cube -> cube.getPoints().map {point -> point to cube } }.toMap()
    var count = 0
    for (cube in sortedAfterDrop) {
        val cubesAbove = cube.getPoints().map { point -> cubeMap[point.add(Point3d.DIRECTION_ZUP)] }.distinct()
        if (cubesAbove.isEmpty()) {
            count++
        } else {
            val cubesBelow = cubeMap.filter { key, value -> key.z < cube.origin.z}
        }


        if (others.all { other -> other == dropCube(other, others.filterNot { it == other }) }) {
            println("Can remove: ${'A' + cube.label}")
            count++
        }
    }
    println("Num: $count") // 395
}

fun printCubeStack(cubes: List<Cube>) {
    val maxX = cubes.maxOf { it.origin.x + it.vector.x }
    val maxY = cubes.maxOf { it.origin.y + it.vector.y }
    val maxZ = cubes.maxOf { it.origin.z + it.vector.z }
    println("size: [$maxX,$maxY,$maxZ]")
    val pointToCube: Map<Point3d, Cube> = cubes.flatMap { cube -> cube.getPoints().map { point -> point to cube } }.toMap()
    for (z in maxZ downTo 1) {
        println("Z: $z")
        for (y in 0 .. maxY) {
            for (x in 0 .. maxX) {
                val current = Point3d(x, y, z)
                val cube = pointToCube[current]
                if (cube == null) {
                    print(".")
                } else {
                    val value: Char = 'A' + cube.label
                    print(value)
                }
                print("|")
            }
            println("")
        }
        println("-".repeat((maxX + 1) * 2))
    }
}

fun dropCube(cube: Cube, droppedCubes: Collection<Cube>): Cube {
    var newOrigin = cube.origin
    val droppedPoints = droppedCubes.flatMap { it.getPoints() }.toSet()
    while (newOrigin.z > 1) {
        val newCube = cube.withOrigin(newOrigin)
        val points = newCube.getPoints()
        val newPoints = points.map { it.add(Point3d.DIRECTION_ZDOWN)}
        if (newPoints.any { droppedPoints.contains(it) }) {
            break
        } else {
            newOrigin = newOrigin.add(Point3d.DIRECTION_ZDOWN)
        }
    }
    return Cube(cube.label, newOrigin, cube.vector)
}

fun parseCube(index: Int, line: String): Cube {
    val (left, right) = line.split("~")

    val origin = parsePoint3d(left)
    val extent = parsePoint3d(right)

    return Cube(index, origin, extent.subtract(origin))
}

fun parsePoint3d(pointStr: String): Point3d {
    val (x, y, z) = pointStr.split(",")
    return Point3d(x.toInt(), y.toInt(), z.toInt())
}

data class Cube(val label: Int, val origin: Point3d, val vector: Point3d) {

    fun withOrigin(newOrigin: Point3d): Cube =
        Cube(label, newOrigin, vector)

    fun getPoints(): List<Point3d> {
        if (vector == Point3d.ORIGIN) {
            return listOf(origin)
        }
        val magnitude = vector.getDirectionMagnitude()
        val direction = vector.toDirection()
        var acc = Point3d.ORIGIN
        return (0 .. magnitude).map {
            origin.add(acc).also { acc = acc.add(direction)}
        }
    }
    override fun toString(): String = "origin=$origin, vector=$vector"
}

data class Point3d(val x: Int, val y: Int, val z: Int) {

    fun isDirectional(): Boolean {
        return if (x != 0) {
            y == 0 && z == 0
        } else if (y != 0) {
            z == 0  // x == 0 is implied
        } else {
            z != 0 // x == 0 && y == 0 are implied
        }
    }

    fun toDirection(): Point3d {
        require(isDirectional())
        return if (x != 0) {
            Point3d(x/x, 0, 0)
        } else if (y != 0) {
            Point3d(0, y/y, 0)
        } else {
            require(z != 0)
            Point3d(0, 0, z/z)
        }
    }

    fun getDirectionMagnitude(): Int {
        require(isDirectional())
        return if (x != 0) {
            x
        } else if (y != 0) {
            y
        } else {
            z
        }
    }

    fun add(other: Point3d): Point3d = Point3d(x + other.x, y + other.y, z + other.z)

    fun subtract(other: Point3d): Point3d = Point3d(x - other.x, y - other.y,z - other.z)

    override fun toString(): String = "[$x,$y,$z]"

    companion object {
        val ORIGIN = Point3d(0, 0, 0)
        val DIRECTION_ZUP = Point3d(0, 0, 1)
        val DIRECTION_ZDOWN = Point3d(0, 0, -1)
    }
}
