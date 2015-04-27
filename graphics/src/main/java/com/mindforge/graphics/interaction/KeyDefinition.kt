package com.mindforge.graphics.interaction

import com.mindforge.graphics.*

trait KeyDefinition {
    val command: Command
    val alternativeCommands: Iterable<Command> get() = listOf()
    val name: String get() = command.name
}

fun keyDefinition(command: Command, alternativeCommands: Iterable<Command> = listOf(), name: String = command.name) = object : KeyDefinition {
    override val command = command
    override val alternativeCommands = alternativeCommands
    override val name = name
}

object KeyDefinitions {
    fun left(command: Command, alternativeCommands: Iterable<Command> = listOf()) = keyDefinition(command, alternativeCommands, "left ${command.name}")
    fun right(command: Command, alternativeCommands: Iterable<Command> = listOf()) = keyDefinition(command, alternativeCommands, "right ${command.name}")
    fun numPad(command: Command, alternativeCommands: Iterable<Command> = listOf()) = keyDefinition(command, alternativeCommands, "num pad ${command.name}")
}