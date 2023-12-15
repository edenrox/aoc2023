package com.hopkins.aoc.day15

import java.io.File

const val debug = true
const val part = 2

val map: Map<Int, MutableList<Lens>> =
    (0 until 256).associateBy({ it }, { mutableListOf()})

/** Advent of Code 2023: Day 15 */
fun main() {
    // Read the file input
    val lines: List<String> = File("input/input15.txt").readLines()
    val numLines = lines.size
    if (debug) {
        println("Num lines: $numLines")
    }

    // Split into items
    val items = lines[0].split(",")
    if (debug) {
        println("Num items: ${items.size}")
    }

    val sum = items.sumOf { item ->
        val result = calculateHash(item)
        if (debug) {
            println("item=$item hash=$result")
        }
        result
    }
    println("Part 1 Sum: $sum")
    println()

    if (part == 1) {
        return
    }

    items.forEach { item ->
        val label = item.dropLast(if (item.endsWith("-")) 1 else 2)
        val boxNumber = calculateHash(label)
        println("Item: $item Label: $label Box: $boxNumber")

        val box = map[boxNumber]!!
        if (item.endsWith("-")) {
            // Remove the item
            box.removeAll { lens -> lens.label == label }
            println("Remove $label")
        } else {
            // Add the item
            val (_, focalLength) = item.split("=")
            val focalLengthInt = focalLength.toInt()
            val lens = box.firstOrNull { lens -> lens.label == label }
            if (lens == null) {
                box.add(Lens(label, focalLengthInt))
            } else {
                lens.focalLength = focalLengthInt
            }
        }
    }


    val p2sum = map.entries
        .filter { (_, lensList) -> lensList.isNotEmpty() }
        .map { (boxNumber, lensList) ->
            val lensValues =
                lensList.mapIndexed { index, lens ->
                    (boxNumber + 1) * (index + 1) * lens.focalLength
                }.sum()
            println("Box $boxNumber: lenses=$lensList value=$lensValues")
            lensValues
        }.sum()
    println("Part 2 Sum: $p2sum")
}

fun calculateHash(input: String): Int {
    if (input.isEmpty()) {
        return 0;
    }
    var acc = 0
    for (c in input.chars()) {
        acc += c
        acc *= 17
        acc %= 256
    }
    return acc
}

class Lens(val label: String, var focalLength: Int) {

    override fun toString(): String = "[$label $focalLength]"
}