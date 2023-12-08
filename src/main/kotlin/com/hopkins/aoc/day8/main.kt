package com.hopkins.aoc.day8

import java.io.File

const val debug = true
const val start = "AAA"
const val end = "ZZZ"

/** Advent of Code 2023: Day 8 */
fun main() {
    val inputFile = File("input/input8.txt")
    val lines: List<String> = inputFile.readLines()

    val directions = lines[0]

    val nodes: List<Node> = lines.drop(2).map { parseNode(it) }
    if (debug) {
        nodes.take(5).forEach { println("Node: $it") }
    }

    val nodeMap: Map<String, Node> = nodes.associateBy { it.name }

    var current = nodeMap[start]!!
    var index = 0
    while (current.name != end) {
        val currentDirection = directions[index % directions.length]
        index++
        val nextName =
        if (currentDirection == 'L') {
            current.left
        } else {
            current.right
        }
        current = nodeMap[nextName]!!
    }
    println("Steps: $index")
}

fun parseNode(line: String): Node {
    val name = line.substring(0, 3)
    val left = line.substring(7, 10)
    val right = line.substring(12, 15)
    return Node(name, left, right)
}

class Node(val name: String, val left: String, val right: String) {

    override fun toString(): String = "Node {name=$name, left=$left, right=$right}"
}