package com.hopkins.aoc.day9

import java.io.File

const val debug = true
const val part = 2

/** Advent of Code 2023: Day 9 */
fun main() {
    val inputFile = File("input/input9.txt")
    val lines: List<String> = inputFile.readLines()

    val sequences = lines.map { line -> parseSequence(line) }
    if (debug) {
        println("Sequences: ")
        sequences.take(5).forEach { println(it) }
    }

    val nextValues = sequences.map { findNextValue(it) }
    if (debug) {
        println("Next Values: ")
        nextValues.take(5).forEach { println(it) }
    }

    val result = nextValues.sum()
    println("Result: $result")
}

fun parseSequence(line: String) : List<Long> =
    line.split(" ").map { it.toLong() }

fun findNextValue(sequence: List<Long>): Long {
    if (sequence.sum() == 0L) {
        return 0L
    }
    if (part == 1) {
        return sequence.last() + findNextValue(findDiffs(sequence))
    } else {
        return sequence.first() - findNextValue(findDiffs(sequence))
    }
}

fun findDiffs(sequence: List<Long>): List<Long> =
    sequence.dropLast(1).zip(sequence.drop(1)).map { (first, second) -> second - first}