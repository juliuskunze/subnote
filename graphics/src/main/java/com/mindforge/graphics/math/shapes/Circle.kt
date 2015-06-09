package com.mindforge.graphics.math

import com.mindforge.graphics.Vector2
import com.mindforge.graphics

interface Circle : Ellipse {
    val radius: Number
    override val size: Vector2 get() = graphics.vector(2 * radius.toDouble(), 2 * radius.toDouble())
}

fun circle(radius: Number) = object : Circle {
    override val radius = radius
}