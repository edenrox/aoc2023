package com.hopkins.aoc.day8

import java.io.File

const val debug = true
const val part = 2

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
    nodes.forEach { it.initialize(nodeMap) }

    val starts: List<String> = getStarts(nodeMap.keys)
    if (debug) {
        println("Starts:")
        println(" $starts")
    }
    var current: List<Node> = starts.map { nodeMap[it]!! }
    var index = 0L
    var cycles = starts.map { 0L }.toMutableList()
    while (cycles.contains(0L)) {
        val dirIndex = (index % directions.length).toInt()
        val currentDirection = directions[dirIndex]
        index++

        current = current.map { node ->
            if (currentDirection == 'L') {
                node.leftNode
            } else {
                node.rightNode
            }
        }
        if (debug && index < 10) {
            println("index: $index, current: ${current.map { it.name }}")
        }
        current.mapIndexed { nodeIndex, node ->
            if (cycles[nodeIndex] == 0L && node.name.endsWith("Z")) {
                cycles[nodeIndex] = index
                println("nodeIndex: $nodeIndex, index: $index")
            }}
    }
    if (part == 1) {
        println("Steps: ${cycles[0]}")
        return
    }

    if (debug) {
        println("Cycles: $cycles")
    }
    val lcm = cycles.fold(1L) { a, b -> lowestCommonMultiple(a, b) }
    println("Lowest Common Multiple: $lcm")
}

fun lowestCommonMultiple(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

fun parseNode(line: String): Node {
    val name = line.substring(0, 3)
    val left = line.substring(7, 10)
    val right = line.substring(12, 15)
    return Node(name, left, right)
}

fun getStarts(nodes: Collection<String>): List<String> {
    if (part == 1) {
        return listOf("AAA")
    } else {
        return nodes.filter { it.endsWith("A") }
    }
}

fun isAtEnd(nodes: List<Node>): Boolean {
    if (part == 1) {
        return nodes[0].name == "ZZZ"
    } else {
        return nodes.all { it.name.endsWith("Z") }
    }
}

class Node(val name: String, val left: String, val right: String) {
    lateinit var leftNode: Node
    lateinit var rightNode: Node

    fun initialize(nodeMap: Map<String, Node>) {
        leftNode = nodeMap[left]!!
        rightNode = nodeMap[right]!!
    }


    override fun toString(): String = "Node {name=$name, left=$left, right=$right}"
}