package com.hopkins.aoc.day5

import java.io.File

const val debug = true
enum class MapType {
    SOIL,
    FERTILIZER,
    WATER,
    LIGHT,
    TEMPERATURE,
    HUMIDITY,
    LOCATION
}

/** Advent of Code 2023: Day 5 */
fun main() {
    val inputFile = File("input/input5.txt")
    val lines: List<String> = inputFile.readLines()
    var seeds: List<Long> = emptyList()
    var maps: MutableMap<MapType, MutableList<MapRange>> = mutableMapOf()
    for (type in MapType.values()) {
        maps[type] = mutableListOf()
    }
    var currentType: MapType? = null
    lines.map { it.trim() } .forEach { line ->
        when {
            line.isEmpty() -> false // skip this line
            line.startsWith("seeds:") -> seeds = readSeeds(line)
            line.endsWith("map:") -> currentType = readMapType(line)
            else -> {
                if (debug) {
                    println("read $currentType map line")
                }
                maps[currentType!!]!!.add(readMapLine(line))
            }
        }
    }

    if (debug) {
        println("Seeds: $seeds")

        println("Soil Map: ${maps[MapType.SOIL]}")
    }
    var output: MutableMap<Long, Long> = mutableMapOf()
    for (seed in seeds) {
        var current = seed
        for (type in MapType.values()) {
            val ranges: List<MapRange> = maps[type]!!
            ranges.filter { it.contains(current) }
            for (range in ranges) {
                if (range.contains(current)) {
                    current = range.convert(current)
                    break
                }
            }
        }
        output[seed] = current
    }
    if (debug) {
        println("Output: $output")
    }
    val minLocation = output.minBy { it.value }.value
    println("Result: $minLocation")
}

class MapRange(val sourceOffset: Long, val destOffset: Long, val length: Long) {
    fun contains(value: Long): Boolean =
        value >= sourceOffset && value < sourceOffset + length

    fun convert(value: Long): Long =
        destOffset + (value - sourceOffset)

    override fun toString(): String =
        "Range {source=[$sourceOffset-${sourceOffset+length-1}] dest=[$destOffset-${destOffset + length-1}]}"
}
fun readMapType(line: String): MapType {
    val lastDash = line.lastIndexOf("-")
    val lastMap = line.lastIndexOf(" map:")
    val name = line.substring(lastDash + 1, lastMap).uppercase()
    return MapType.valueOf(name)
}

fun readMapLine(line: String): MapRange {
    val (destOffset, sourceOffset, length) = line.split(" ").map { it.toLong() }
    return MapRange(sourceOffset, destOffset, length)
}

fun readSeeds(line: String): List<Long> {
    if (debug) {
        println("read seeds")
    }
    val (_, seeds) = line.split(":")
    return seeds.trim().split(" ").map { it.toLong() }
}