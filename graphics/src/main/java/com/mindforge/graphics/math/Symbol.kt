package com.mindforge.graphics.math

public trait Symbol : Real {
    val name: String

    val unit: String? // TODO: Create a dedicated Unit Type

    override fun toString() = name

    override fun equals(other: Any?): Boolean = other is Symbol && name == other.name
}