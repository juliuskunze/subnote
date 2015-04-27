package com.mindforge.graphics.math

import com.mindforge.graphics.Vector2

trait Rectangle : Shape {
    val size: Vector2
    val halfSize: Vector2 get() = size / 2

    override fun contains(location: Vector2) = Math.abs(location.x.toDouble()) <= halfSize.x.toDouble() && Math.abs(location.y.toDouble()) <= halfSize.y.toDouble()
}

fun rectangle(size: Vector2) = object : Rectangle {
    override val size = size
}