package com.hopkins.aoc.day20

import java.io.File

const val debug = true
const val part = 1

/** Advent of Code 2023: Day 20 */
fun main() {
    // Step 1: Read the file input
    val lines: List<String> = File("input/input20.txt").readLines()
    if (debug) {
        println("Step 1: Read file")
        println("=======")
        println("  num lines: ${lines.size}")
    }

    // Step 2: Parse the modules
    val modules: List<Module> =
        listOf(Module("button", ModuleType.BUTTON, listOf("broadcaster"))) +
                lines.filter { it.isNotBlank() }.map { parseModule(it) }
    val moduleMap = modules.associateBy { it.label }
    val moduleInputs =
        modules.flatMap { module ->
            module.outputs.map {
                    output -> output to module.label } }
            .groupBy({ it.first}, {it.second})

    val moduleStateMap = modules.associateBy({it.label}, {it.generateState(moduleInputs[it.label] ?: emptyList())})
    if (debug) {
        println("Step 2: Parse modules")
        println("=======")
        modules.forEach { println("  $it") }
    }

    // Step 3: Simulate a button press
    var lowCount = 0L
    var highCount = 0L
    for (i in 0 until 1000) {
        val current = mutableListOf(Pulse(PulseType.LOW, "button", "broadcaster"))
        while (current.isNotEmpty()) {
            val pulse = current.removeFirst()
            //println("pulse=$pulse, currentSize=${current.size}")
            if (pulse.type == PulseType.LOW) {
                lowCount++
            } else {
                highCount++
            }
            val nextPulses = sendPulse(pulse, moduleMap, moduleStateMap)
            current.addAll(nextPulses)
        }
    }

    println("low=$lowCount high=$highCount")
    println("result=${lowCount * highCount}")
}

fun sendPulse(
    toSend: Pulse,
    moduleMap: Map<String, Module>,
    moduleStateMap: Map<String, ModuleState>): List<Pulse> {
    val destLabel = toSend.destination
    val destModule = moduleMap[destLabel] ?: return emptyList()
    val destState = moduleStateMap[destLabel]!!
    destState.update(toSend)
    if (destModule.type == ModuleType.BROADCAST) {
        return destModule.producePulses(toSend.type, destLabel)
    } else if (destModule.type == ModuleType.FLIP_FLOP) {
        if (toSend.type == PulseType.HIGH) {
            return emptyList()
        } else {
            val state = destState as FlipFlopState
            val type = if (state.isOn()) PulseType.HIGH else PulseType.LOW
            return destModule.producePulses(type, destLabel)
        }
    } else if (destModule.type == ModuleType.CONJUNCTION) {
        val state = destState as ConjunctionState
        val type = if (state.isAllHigh()) PulseType.LOW else PulseType.HIGH
        return destModule.producePulses(type, destLabel)
    } else {
        throw IllegalArgumentException("Unexpected type: ${destModule.type}")
    }
}


fun parseModule(line: String): Module {
    var (left, right) = line.split(" -> ")
    var outputs = right.split(", ")
    return if (left == "broadcaster") {
        Module(left, ModuleType.BROADCAST, outputs)
    } else {
        require(left.first() in listOf('%', '&'))
        var firstChar = left.first()
        val type = when (firstChar) {
            '%' -> ModuleType.FLIP_FLOP
            '&' -> ModuleType.CONJUNCTION
            else -> throw IllegalArgumentException("Unexpected type: $firstChar")
        }
        Module(left.drop(1), type, outputs)
    }
}

enum class ModuleType {
    BUTTON,
    BROADCAST,
    FLIP_FLOP,
    CONJUNCTION,
}

enum class PulseType {
    LOW,
    HIGH
}

data class Module(
    val label: String,
    val type: ModuleType,
    val outputs: List<String>) {

    fun generateState(inputs: List<String>): ModuleState {
        return when (type) {
            ModuleType.CONJUNCTION -> ConjunctionState(inputs)
            ModuleType.FLIP_FLOP -> FlipFlopState()
            else -> EmptyState
        }
    }

    fun producePulses(type: PulseType, source: String): List<Pulse> =
        outputs.map { output -> Pulse(type, source, output) }
}

interface ModuleState {
    fun update(pulse: Pulse)
}

object EmptyState : ModuleState {
    override fun update(pulse: Pulse) {}
}

class FlipFlopState: ModuleState {
    private var isOn: Boolean = false

    fun isOn(): Boolean = isOn

    override fun update(pulse: Pulse) {
        if (pulse.type == PulseType.LOW) {
            isOn = !isOn
        }
    }
}

class ConjunctionState(inputs: List<String>): ModuleState {
    private val lastReceived = inputs.associateBy({it}, {PulseType.LOW}).toMutableMap()

    fun isAllHigh(): Boolean =
        lastReceived.values.all { it == PulseType.HIGH }

    override fun update(pulse: Pulse) {
        lastReceived[pulse.source] = pulse.type
    }
}

data class Pulse(
    val type: PulseType,
    val source: String,
    val destination: String
)