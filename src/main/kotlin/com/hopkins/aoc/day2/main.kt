package com.hopkins.aoc.day2

import java.io.File

const val debug = false
const val part = 2
val maxCubes = CubeInfo(red = 12, green = 13, blue = 14)

/** Advent of Code 2023: Day 2 */
fun main() {
    val inputFile = File("input/input2.txt")
    if (debug) {
        println("Max Cubes: $maxCubes")
    }

    // Extract the GameInfo from each line
    val gameInfoList: List<GameInfo> = inputFile.readLines().map { line -> parseGame(line) }
    if (debug) {
        gameInfoList.take(5).map { println("Game: ${it.id} isValid: ${it.isValid(maxCubes)}") }
    }

    if (part == 1) {
        // Sum the Game IDs where the game is valid given the max number of cubes
        val gameIdSum: Int = gameInfoList.filter { it.isValid(maxCubes) }.map { it.id }.sum()

        println("gameIdSum: $gameIdSum") // 2771
    } else {
        val powerSum: Int = gameInfoList.sumOf { game ->
            val minCubes = findMinCubes(game.cubes)
            minCubes.red * minCubes.green * minCubes.blue
        }
        println("Power Sum: $powerSum") // 70924
    }
}

/** Returns the minimum number of cubes of each color required to play the specified set of games. */
fun findMinCubes(cubes: List<CubeInfo>): CubeInfo =
    CubeInfo(
        cubes.maxOf { it.red },
        cubes.maxOf { it.green },
        cubes.maxOf { it.blue }
    )

/** Parse a [GameInfo] from a line of input. */
fun parseGame(line: String): GameInfo {
    // Example line:
    // Game <Id>: <CubeInfo>; <CubeInfo>; ...
    val parts = line.split(":", ";")
    val id = parts[0].substring(startIndex = 5).toInt()
    return GameInfo(id, parts.drop(1).map { parseCubeInfo(it) })
}

/** Parse a [CubeInfo] from a single draw. */
fun parseCubeInfo(cubeInfo: String): CubeInfo {
    // Example draw:
    // <X> red, <Y> green, <Z> blue
    val parts = cubeInfo.split(",")
    var (red, green, blue) = listOf(0, 0, 0)

    parts.map { parseCube(it) }.forEach { (color, num) ->
        when (color) {
            "red" -> red += num
            "green" -> green += num
            "blue" -> blue += num
        }
    }
    return CubeInfo(red, green, blue)
}

/** Parse the color and count of cubes drawn. */
fun parseCube(cube: String): Pair<String, Int> {
    // Example cube
    val parts = cube.trim().split(" ")
    return Pair(parts[1], parts[0].toInt())
}

class GameInfo(val id: Int, val cubes: List<CubeInfo>) {
    /** Returns `true` if the cube draws of this game are valid given the specifed [maxCubes]. */
    fun isValid(maxCubes: CubeInfo): Boolean = !cubes.any { it.isAnyGreaterThan(maxCubes) }

    override fun toString(): String = "GameInfo id=${id} cubes=${cubes}"
}

class CubeInfo(val red: Int, val green: Int, val blue: Int) {
    /** Returns `true` if any of the draws are greater than the specified max number of cubes. */
    fun isAnyGreaterThan(max: CubeInfo): Boolean =
        red > max.red || green > max.green || blue > max.blue

    override fun toString(): String = "r=${red} g=${green} b=${blue}"
}
