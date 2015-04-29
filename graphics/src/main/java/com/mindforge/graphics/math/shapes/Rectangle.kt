package com.mindforge.graphics.math

import com.mindforge.graphics.Transforms2
import com.mindforge.graphics.Vector2

trait Rectangle : Shape {
    val size: Vector2
    val halfSize: Vector2 get() = size / 2

    override fun contains(location: Vector2) = Math.abs(location.x.toDouble()) <= halfSize.x.toDouble() && Math.abs(location.y.toDouble()) <= halfSize.y.toDouble()
}

fun rectangle(size: Vector2) = object : Rectangle {
    override val size = size
}

fun Rectangle.bottomLeftAtOrigin() = transformed(Transforms2.translation(size / 2))
fun Rectangle.topLeftAtOrigin() = transformed(Transforms2.translation(size / 2) before Transforms2.scale(1, -1))
fun Rectangle.topRightAtOrigin() = transformed(Transforms2.translation(size / 2) before Transforms2.scale(-1, -1))
fun Rectangle.bottomRightAtOrigin() = transformed(Transforms2.translation(size / 2) before Transforms2.scale(-1, 1))