package com.hopkins.aoc.day7

import java.io.File

const val debug = true

val allCards = listOf("A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2", "1")

enum class HandType {
    FIVE_OF_A_KIND,
    FOUR_OF_A_KIND,
    FULL_HOUSE,
    THREE_OF_A_KIND,
    TWO_PAIR,
    ONE_PAIR,
    HIGH_CARD
}

/** Advent of Code 2023: Day 7 */
fun main() {
    val inputFile = File("input/input7.txt")
    val lines: List<String> = inputFile.readLines()

    val hands: List<CamelHand> = lines.map { readHand(it) }
    if (debug) {
        println("Hands:")
        hands.take(5).forEach { println(" $it") }
    }
    val sortedHands: List<CamelHand> =
        hands.sortedWith(compareBy( { it.type }, { it.getCardsValues() } ))
    if (debug) {
        println("Sorted Hands:")
        sortedHands.take(5).forEach { println(" $it") }
    }

    val topValue = hands.size
    val handValues: List<Long> =
        sortedHands.mapIndexed { index, camelHand ->  ((topValue - index) * camelHand.bid).toLong() }
    if (debug) {
        println("Top Value: $topValue")
        println("Hand values:")
        handValues.take(5).forEach { println(" $it") }
    }

    val result = handValues.sum()
    println("Result: $result")
}

/** Returns a [CamelHand] instance parsed from the specified line. */
fun readHand(line: String): CamelHand {
    // Format: <Cards> <Bid>
    // Example: 32T3K 765
    val (cards, bidString) = line.trim().split(" ")
    return CamelHand(cards, bidString.toInt())
}

/** Figure out the best hand which can be formed from the specified cards. */
fun calculateType(cards: String) : HandType {
    // Group and count cards by type, then sort descending
    val cardCountList: List<Int> = cards.groupingBy { it }.eachCount().values.sortedDescending()

    val max = cardCountList.first()
    return when (max) {
        5 -> HandType.FIVE_OF_A_KIND
        4 -> HandType.FOUR_OF_A_KIND
        3 -> if (cardCountList[1] == 2) { HandType.FULL_HOUSE } else { HandType.THREE_OF_A_KIND }
        2 -> if (cardCountList[1] == 2) { HandType.TWO_PAIR } else { HandType.ONE_PAIR }
        else -> HandType.HIGH_CARD
    }
}

/** Represents a set of 5 cards and a bid value. */
class CamelHand(val cards: String, val bid: Int) {
    val type: HandType = calculateType(cards)

    /** Returns a string that represents the value of the cards (00 being high, 13 being low). */
    fun getCardsValues(): String =
        cards.map { allCards.indexOf(it.toString()) }.joinToString("") { String.format("%02d", it) }

    override fun toString(): String = "Hand {type=$type cards=$cards cardValues=${getCardsValues()} bid=$bid}"
}

