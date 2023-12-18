package com.hopkins.aoc.day17

import java.io.File
import kotlin.math.abs

const val debug = true
const val part = 1

lateinit var mapSize: Point
lateinit var map: Map<Point, Int>
lateinit var endPoint: Point
val minDistanceLookup = mutableMapOf<PathKey, Int>()

/** Advent of Code 2023: Day 17 */
fun main() {
    // Step 1: Read the file input
    val lines: List<String> = File("input/input17.txt").readLines()
    if (debug) {
        println("Step 1: Read file")
        println("=======")
        println("  num lines: ${lines.size}")
    }

    // Step 2: Parse the map
    val mapWidth = lines[0].length
    val mapHeight = lines.size
    mapSize = Point.of(mapWidth, mapHeight)
    map =
        lines.flatMapIndexed { y, line ->
            line.mapIndexed { x, c ->
                Point.of(x, y) to c.digitToInt()
            }
        }.toMap()
    endPoint = Point.of(mapWidth - 1, mapHeight - 1)
    if (debug) {
        println("\nStep 2: Build map")
        println("=======")
        if (debug) {
            println("  map size: $mapSize")
        }
        println("Map")
        println("===")
        for (y in 0 until mapHeight) {
            print("  ")
            for (x in 0 until mapWidth) {
                print(map[Point.of(x, y)])
            }
            println()
        }
    }

    // Step 3: Build up a list of all points in the map
    val allPoints =
        (0 until mapHeight).flatMap { y ->
            (0 until mapWidth).map { x ->
                Point.of(x, y)
            }
        }
    if (debug) {
        println("\nStep 3: Build all points list")
        println("=======")
        println("  Num points: ${allPoints.size}")
    }

    // Step 4: Build a lookup of minimum distance from each path to the end
    for (start in allPoints) {
        val key = PathKey.of(start, endPoint)
        minDistanceLookup[key] = findBestDirectPathCached(start, endPoint)
    }
    if (debug) {
        println("\nStep 4: Build minimum distance map")
        println("=======")
        for (y in 0 until mapHeight) {
            print("  ")
            for (x in 0 until mapWidth) {
                if (x > 0) print(" | ")
                val key = PathKey.of(Point.of(x, y), endPoint)
                print("%03d".format(minDistanceLookup[key]!!))
            }
            println()
        }
    }

    // Step 5: find the best path via A* search
    val comparator = compareBy<Path>({ it.distance }, { it.estimatedCost })

    var count = 0
    for (offsetA in 1 until mapWidth) {
        for (offsetB in offsetA - 1 downTo 0) {
            val cornerPoint = endPoint.subtract(Point.of(offsetA, offsetA))
            val startPoints = setOf(
                cornerPoint.add(Point.of(offsetB, 0)),
                cornerPoint.add(Point.of(0, offsetB))
            )
            for (startPoint in startPoints) {
                var bestPaths = listOf(Path(0, listOf(startPoint)))
                var currentBestLookup = mutableMapOf<Point, Int>()

                while (bestPaths.isNotEmpty()) {
                    count++
                    if (count == 1000000) {
                        println(".")
                        count = 0
                    }
                    val bestPath = bestPaths.first()
                    val lastPoint = bestPath.points.last()
                    //println("Last point= $lastPoint")
                    val newPaths = mutableListOf<Path>()
                    val bestFinal = currentBestLookup[endPoint]
                    for (direction in directions) {
                        val nextPoint = lastPoint.add(direction)
                        if (!isInBounds(nextPoint, mapSize)) {
                            continue
                        }
                        if (nextPoint.x < cornerPoint.x || nextPoint.y < cornerPoint.y) {
                            continue
                        }
                        if (direction.isOpposite(bestPath.lastDirection)) {
                            continue
                        }
                        if (direction == bestPath.lastThreeDirections) {
                            continue
                        }
                        val cost = map[nextPoint]!!
                        val newCost = bestPath.cost + cost
                        val estimatedCost = newCost + minDistanceLookup[PathKey.of(nextPoint, endPoint)]!!
                        if (bestFinal != null && estimatedCost > bestFinal) {
                            continue
                        }
                        val newPath = Path(newCost, bestPath.points + listOf(nextPoint))
                        val currentBest = currentBestLookup[nextPoint]
                        if (currentBest == null || currentBest >= newPath.cost) {
                            currentBestLookup[nextPoint] = newPath.cost
                        }
                        newPaths.add(newPath)
                    }


                    bestPaths = (bestPaths.drop(1) + newPaths)
                        .filter { it.estimatedCost < (bestFinal ?: Int.MAX_VALUE) }
                        .sortedWith(comparator)
                }

                if (startPoint == cornerPoint) {
                    println("start=$startPoint")
                    println("  best: ${currentBestLookup[endPoint]}")
                    // Incorrect [0,0]: 853
                }
                minDistanceLookup[PathKey.of(startPoint, endPoint)] = currentBestLookup[endPoint]!!
            }
        }
    }
}

val directPathCache = mutableMapOf<PathKey, Int>()
val bestDirections = listOf(Point.of(1, 0), Point.of(0, 1))

fun findBestDirectPathCached(start: Point, end: Point): Int {
    val key = PathKey.of(start, end)
    val cached = directPathCache[key]
    if (cached != null) {
        return cached
    }
    val result = findBestDirectPath(start, end)
    directPathCache[key] = result
    return result
}
fun findBestDirectPath(start: Point, end: Point): Int {
    if (start == end) {
        return 0
    } else if (start.distanceTo(end) == 1) {
        return map[end]!!
    } else {
        return bestDirections
            .map { start.add(it) }
            .filter { isInBounds(it, mapSize) }
            .minOf { map[it]!! + findBestDirectPathCached(it, end) }
    }
}

fun printPath(path: Path, mapSize: Point) {
    val pointSet = path.points.toSet()
    for (y in 0 until mapSize.x) {
        for (x in 0 until mapSize.y) {
            val point = Point.of(x, y)
            if (pointSet.contains(point)) {
                print("#")
            } else {
                print(".")
            }
        }
        println()
    }
    val cost = path.cost
    println("Cost: $cost")
}

fun calculateBestPaths(start: Point, end: Point, bestPathMap: Map<PathKey, List<Path>>): List<Path> {
    val bestExistingPaths = bestPathMap[PathKey.of(start, end)]

    // Find the points we need to check paths for
    val pointsToTest = directions.map { start.add(it) }.filter { isInBounds(it, mapSize) }

    // Generate the new paths
    val newPaths = pointsToTest.flatMap { point ->
        val key = PathKey.of(point, end)
        val paths = bestPathMap[key]
        if (paths == null) {
            emptyList()
        } else {
            paths
                .map { path -> Path(path.cost + map[point]!!, listOf(start) + path.points)  }
                .filter { it.hasValidStart() }
        }
    }

    // Only keep track of the two best paths
    return ((bestExistingPaths ?: emptyList()) + newPaths).distinct().sortedBy { it.cost }.take(2)
}

data class PathKey private constructor(val start: Point, val end: Point) {

    companion object {
        private val cache = mutableMapOf<Point, PathKey>()

        fun of(start: Point, end: Point): PathKey {
            return if (end == endPoint) {
                cache.computeIfAbsent(start) { start -> PathKey(start, end) }
            } else {
                PathKey(start, end)
            }
        }
    }

}
fun isInBounds(point: Point, size: Point) =
    point.x >= 0 && point.y >= 0 && point.x < size.x && point.y < size.y


val directions = listOf(
    Point.of(-1, 0),
    Point.of(1, 0),
    Point.of(0, -1),
    Point.of(0, 1)
)
data class Path(val cost: Int, val points: List<Point>) {
    val estimatedCost = cost + minDistanceLookup[PathKey.of(points.last(), endPoint)]!!
    val distance = points.last().distanceTo(endPoint)

    val lastDirection =
        if (points.size < 2) {
            Point.ORIGIN
        } else {
            val (a, b) = points.takeLast(2)
            b.subtract(a)
        }
    val lastThreeDirections =
        if (points.size < 4) {
            Point.ORIGIN
        } else {
            val (d1, d2, d3) = points.takeLast(4).zipWithNext().map { (a, b) -> b.subtract(a) }
            if (d1 == d2 && d2 == d3) {
                d1
            } else {
                Point.ORIGIN
            }
        }

    fun getTail(): Path = Path(0, points.takeLast(4))

    fun findNextPaths(map: Map<Point, Int>): List<Path> {
        val lastPoint = points.last()
        return directions
            // Filter paths that go out of bounds
            .filter { isInBounds(lastPoint.add(it), mapSize) }
            // Filter paths that are not allowed
            .filter { isDirectionAllowed(it) }
            .map { direction ->
                val nextPoint = lastPoint.add(direction)
                val nextCost = cost + map[nextPoint]!!
                Path(nextCost, points + listOf(nextPoint))
            }
    }

    private fun isDirectionAllowed(direction: Point): Boolean {
        if (points.size <= 1) {
            return true
        }
        val (secondLast, last) = points.takeLast(2)
        val lastDirection = last.subtract(secondLast)
        if (direction.isOpposite(lastDirection)) {
            // We can't switch to the opposite direction
            return false
        }
        if (points.size < 4) {
            return true
        }
        val lastDirections = points.takeLast(4).zipWithNext { a, b -> b.subtract(a) }
        if (lastDirections.all { it == direction}) {
            // We can't go in the same direction 4 times in a row
            return false
        }
        return true
    }

    fun hasValidStart(): Boolean {
        if (points.size <= 2) {
            return true
        }

        val directions = points.zipWithNext().map { (a, b) -> b.subtract(a) }
        val (da, db) = directions.takeLast(2)
        for (i in directions.indices) {
            val two = directions.drop(i).take(2)
            if (two.size == 2 && two[0].isOpposite(two[1])) {
                return false
            }
            val four = directions.drop(i).take(4)
            if (four.size == 4 && four[0] == four[1] && four[0] == four[2] && four[0] == four[3]) {
                return false
            }
        }
        return true
    }
}

data class Point private constructor(val x: Int, val y: Int) {

    fun add(dx: Int, dy: Int): Point = of(x + dx, y + dy)

    fun add(delta: Point) = add(delta.x, delta.y)

    fun subtract(other: Point) = of(x - other.x, y - other.y)

    fun isOpposite(other: Point): Boolean = (x == other.x * -1) && (y == other.y * -1)

    fun isOrigin(): Boolean = x == 0 && y == 0

    fun distanceTo(other: Point): Int = abs(other.x - x) + abs(other.y - y)

    companion object {
        private val lookup = (-1 until 200).flatMap {y ->
            (-1 until 200).map {x ->
                Point(x, y)
            }
        }

        val ORIGIN = of(0, 0)

        fun of(x: Int, y: Int): Point {
            if (x >= -1 && x < 200 && y >= -1 && y < 200) {
                return lookup[(y + 1) * 201 + (x + 1)].also { require(it.x == x && it.y == y)}
            } else {
                return Point(x, y)
            }
        }
    }
}