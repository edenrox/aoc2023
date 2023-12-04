package com.hopkins.aoc.day4

import java.io.File

const val debug = false
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
        val cardInfo = extractCardInfo(line)
        val points = cardInfo.getPoints()

        if (debug) {
            println("Card {$cardInfo} points=$points matches=${cardInfo.getMatches()}")
        }
        points
    }
    println("Total Points: $totalPoints") // 23941
}

fun extractCardInfo(line: String): CardInfo {
    // Split into sections around ":" and "|"
    // Example: Card   1: 12 13 | 14 15  4
    val (card, winning, ours) = line.split(":", "|")

    // Extract the card number
    val id: Int = card.substring(5).trim().toInt()

    // Extract winning and our numbers
    return CardInfo(id, extractNumbers(winning).toSet(), extractNumbers(ours).toList())
}

class CardInfo(private val id: Int, private val winners: Set<Int>, private val ours: List<Int>) {
    fun getPoints(): Int =
        scores[getMatches().size]
    fun getMatches(): Collection<Int> =
        ours.intersect(winners)

    override fun toString(): String = "id=$id, winners=$winners, ours=$ours"
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