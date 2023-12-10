package com.hopkins.aoc.day4

import java.io.File

const val debug = true
const val part = 2
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

    if (part == 1) {
        println("Total Points: $totalPoints") // 23941
        return
    }

    val cardMap: Map<Int, CardInfo> =
        lines.map { line -> extractCardInfo(line) }.associateBy { it.id }
    var numCards = 0
    var cards = cardMap.values
    var round = 1
    while (cards.isNotEmpty()) {
        if (debug) {
            println("Round: $round Cards: ${cards.map { it.id }}")
        }
        numCards += cards.size
        round++
        val wonCards = cards.flatMap { card ->
            val numMatches = card.getMatches().size

            IntRange(card.id + 1, card.id + numMatches)
        }.mapNotNull { cardMap[it] }
        cards = wonCards
    }
    println("Num Cards: $numCards")
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

class CardInfo(val id: Int, private val winners: Set<Int>, private val ours: List<Int>) {
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