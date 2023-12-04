package com.hopkins.aoc.day4

import java.io.File

const val debug = true
val scores = buildScores(20)

/** Advent of Code 2023: Day 4 */
fun main() {
    if (debug) {
        println("Scores: $scores")
    }

    val inputFile = File("input/input4.txt")
    val lines: List<String> = inputFile.readLines()

    // Sum the points from each card to get the total
    val totalPoints = lines.sumOf { line ->
        // Split into sections around ":" and "|"
        // Example: Card   1: 12 13 | 14 15  4
        val (card, winning, ours) = line.split(":", "|")

        // Extract the card number
        val cardNumber: Int = card.substring(5).trim().toInt()

        // Extract winning and our numbers
        val winningNumbers = extractNumbers(winning).toSet()
        val ourNumbers = extractNumbers(ours).toList()

        // Calculate our numbers that match the winning set
        val matches = ourNumbers.intersect(winningNumbers)

        // Lookup the number of points our matches are worth
        val points = scores[matches.size]

        if (debug) {
            println("Card $cardNumber: points=$points win=$winningNumbers ours=$ourNumbers")
        }
        points
    }
    println("Total Points: $totalPoints")
}

fun buildScores(count: Int) : List<Int> =
    buildList {
        add(0)
        for (i in 0 until count) {
            add(Math.pow(2.0, i.toDouble()).toInt())
        }
    }

fun extractNumbers(numbers: String): Sequence<Int> {
    return numbers.split(" ")
        .asSequence()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { it.toInt() }
}