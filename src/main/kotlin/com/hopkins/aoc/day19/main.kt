package com.hopkins.aoc.day19

import java.io.File

const val debug = true
const val part = 1

/** Advent of Code 2023: Day 19 */
fun main() {
    // Step 1: Read the file input
    val lines: List<String> = File("input/input19.txt").readLines()
    if (debug) {
        println("Step 1: Read file")
        println("=======")
        println("  num lines: ${lines.size}")
    }

    // Step 2: Parse the workflows and parts
    val workflows = lines.takeWhile { line -> line.isNotBlank() }.map { parseWorkflow(it) }.associateBy { it.label }
    val parts = lines.dropWhile { line -> line.isNotBlank() }.drop(1).mapIndexed { index, line -> parsePart(index, line) }
    if (debug) {
        println("Step 2: Parse workflows and parts")
        println("=======")
        println("  num workflows: ${workflows.size}")
        println("  num parts: ${parts.size}")
        //parts.forEach { println(it) }
        //workflows.forEach { println(it) }
    }

    var total = 0
    for (part in parts) {
        if (debug) {
            println("Mapping part: $part")
        }
        val start = "in"
        var current = start
        print("  ")
        while (current != "A" && current != "R") {
            print("$current -> ")
            val workflow = workflows[current]!!
            val firstApplies = workflow.inequalities.firstOrNull { it.applies(part) }
            current = firstApplies?.output ?: workflow.next
        }
        print(current)
        if (current == "A") {
            val value = part.values.values.sum()
            print(" value=$value")
            total += value
        }
        println()
    }
    print("Total: $total")
}

fun parsePart(index: Int, line: String): Part {
    val pieces = line.substring(1, line.length - 1).split(",")
    val valueMap = pieces.map { piece ->
        val (left, right) = piece.split("=")
        left.first() to right.toInt()
    }.toMap()
    return Part(index, valueMap)
}

fun parseWorkflow(line: String): Workflow {
    val (label, inequalitiesStr) = line.split("{")
    val inequalityPieces = inequalitiesStr.dropLast(1).split(",")
    val next = inequalityPieces.last()
    val inequalities = inequalityPieces.dropLast(1).map { parseInequality(it) }

    return Workflow(label, inequalities, next)
}

fun parseInequality(item: String): Inequality {
    val (left, right) = item.split(":")
    val operator = left[1]
    val value = left.first()
    val quantity = left.substring(2).toInt()
    return Inequality(value, operator, quantity, right)
}

data class Workflow(val label: String, val inequalities: List<Inequality>, val next: String) {

}



data class Inequality(val value: Char, val operator: Char, val quantity: Int, val output: String) {
    fun applies(part: Part): Boolean {
        val partValue = part.values[value]!!
        if (operator == '<' && partValue < quantity) {
            return true
        } else if (operator == '>' && partValue > quantity) {
            return true
        }
        return false
    }
}

data class Part(val num: Int, val values: Map<Char, Int>) {

}