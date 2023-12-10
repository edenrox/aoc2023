package com.hopkins.aoc.day10

import java.io.File

const val debug = false
const val part = 2

/** Advent of Code 2023: Day 9 */
fun main() {
    val inputFile = File("input/input10.txt")
    val lines: List<String> = inputFile.readLines()
    var start = Point(-1, -1)

    val pieceTypeLookup = PieceType.values().associateBy { it.symbol }

    val pieces: List<Piece> =
        lines.flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                if (c == 'S') {
                    start = Point(x, y)
                    null
                } else {
                    val type = pieceTypeLookup.get(c)
                    if (type == null) {
                        null
                    } else {
                        Piece(type, Point(x, y))
                    }
                }
            }}
    if (debug) {
        println("Start: $start")
        println("Num Pieces: ${pieces.size}")
    }
    val pieceMap = pieces.associateBy { it.position }

    if (debug) {
        println("Map:")
        val origin = Point(start.x - 2, start.y - 2)
        for (y in 0 until 10) {
            for (x in 0 until 10) {
                val pos = origin.add(x, y)
                if (start == pos) {
                    print("S")
                } else if (pieceMap.containsKey(pos)) {
                    print(pieceMap.getValue(pos).type.symbol)
                } else {
                    print(".")
                }
            }
            println()
        }
    }

    for (startPieceType in PieceType.values()) {
    //val startPieceType = PieceType.DOWN_RIGHT
        val distance = walkMap(Piece(startPieceType, start), pieceMap)
        if (part == 1 && distance != -1) {
            println("Start Piece: $startPieceType, distance=$distance") // 6725
        }
    }

}

fun printDistanceMap(origin: Point, width: Int, map: Map<Point, Int>) {
    println("Distance Map: ")
    for (y in 0 until width) {
        for (x in 0 until width) {
            val point = origin.add(x, y)
            print(map[point] ?: ".")
        }
        println()
    }
}

fun walkMap(start: Piece, pieceMap: Map<Point, Piece>): Int {
    val distanceMap: MutableMap<Point, Int> = mutableMapOf(start.position to 0)
    var distance = 0
    var currentPieces: List<Piece> = listOf(start, start)
    var nextPoints: List<Point>
    while (true) {
        if (debug) {
            println("Iteration $distance")
            printDistanceMap(Point(0, 0), 6, distanceMap)
        }
        nextPoints =
            if (distance == 0) {
                start.type.positions.map { start.position.add(it) }
            } else {
                // Next point is not where we just came from (distance - 1)
                currentPieces.flatMap { piece ->
                    piece.getConnected().filter { distanceMap[it] != distance - 1 }}
            }
        require(nextPoints.size == 2)
        if (debug) {
            println("Next points: $nextPoints")
        }

        val nextPieces = nextPoints.mapNotNull { pieceMap[it] }
        distance++
        if (nextPieces.size < 2) {
            println("Ground at $nextPoints, distance=$distance")
            return -1
        }
        for (i in 0..1) {
            val nextPiece = nextPieces[i]!!
            val connected = nextPiece.getConnected()
            if (!connected.contains(currentPieces[i].position)) {
                println("Unconnected piece at ${nextPiece.position}")
                return -1
            }
        }
        nextPieces.map { distanceMap[it.position] = distance }
        if (nextPieces[0] == nextPieces[1]) {
            if (part == 2) {
                val max = 140
                val insideSet = mutableSetOf<Point>()
                for (y in 0..max) {
                    for (x in 0..max) {
                        val point = Point(x, y)
                        if (distanceMap[point] == null) {
                            var lastCornerPiece: Piece? = null
                            var crossings = 0
                            for (dx in x..max) {
                                val testPoint = Point(dx, y)
                                if (distanceMap[testPoint] != null) {

                                    val piece = pieceMap[testPoint] ?: start
                                    when (piece.type) {
                                        PieceType.VERTICAL -> crossings++
                                        PieceType.HORIZONTAL -> 0 // noop
                                        else -> if (lastCornerPiece == null) {
                                            lastCornerPiece = piece
                                        } else {
                                            if (lastCornerPiece.type.getVertical() != piece.type.getVertical()) {
                                                crossings++
                                            }
                                            lastCornerPiece = null
                                        }
                                    }

                                }
                            }
                            if (crossings % 2 == 1) {
                                if (debug) {
                                    println("Point: $point crossings: $crossings")
                                }
                                insideSet.add(point)
                            }
                        }
                    }
                }
                println("Inside: ${insideSet.size}")
            }
            return distance
        }
        currentPieces = nextPieces.filterNotNull()
    }
}

enum class PieceType(val symbol: Char, val positions: List<Point>) {
    VERTICAL('|', listOf(Point(0, -1), Point(0, 1))),
    HORIZONTAL('-', listOf(Point(-1, 0), Point(1, 0))),
    DOWN_RIGHT('F', listOf(Point(0, 1), Point(1, 0))),
    DOWN_LEFT('7', listOf(Point(0, 1), Point(-1, 0))),
    UP_RIGHT('L', listOf(Point(0, -1), Point(1, 0))),
    UP_LEFT('J', listOf(Point(0, -1), Point(-1, 0)));

    fun getVertical(): Boolean {
        require(this != VERTICAL && this != HORIZONTAL)
        return this == UP_RIGHT || this == UP_LEFT
    }
}

data class Piece(val type: PieceType, val position: Point) {

    fun getConnected(): List<Point> =
        type.positions.map { position.add(it) }
}

data class Point(val x: Int, val y: Int) {

    fun add(dx: Int, dy: Int) = Point(x + dx, y + dy)

    fun add(other: Point): Point = add(other.x, other.y)
}