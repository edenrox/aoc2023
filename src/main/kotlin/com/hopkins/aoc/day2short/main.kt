package com.hopkins.aoc.day2short
fun main() {
    val max = mapOf("red" to 12, "green" to 13, "blue" to 14)
    println(java.io.File("input/input2.txt").readLines().map { line -> parseLine(line) }.filter { max.keys.all { key -> it.draws.filter { it.first == key }.maxOf { it.second } <= max[key]!! } }.sumOf{ it.id })
}
fun parseLine(line: String): Game {
    val (left, right) = line.split(":")
    return Game(left.substring(5).toInt(), right.split(";").flatMap { it.split(",") }.map { val (num, color) = it.trim().split(" "); color to num.toInt()})
}

data class Game(val id: Int, val draws: List<Pair<String, Int>>)